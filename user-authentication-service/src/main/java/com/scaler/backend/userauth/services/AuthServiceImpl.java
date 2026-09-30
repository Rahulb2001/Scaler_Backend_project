package com.scaler.backend.userauth.services;

import com.scaler.backend.userauth.clients.KafkaProducerClient;
import com.scaler.backend.userauth.dtos.EmailDto;
import com.scaler.backend.userauth.dtos.LoginResponseDto;
import com.scaler.backend.userauth.dtos.UserDto;
import com.scaler.backend.userauth.exceptions.IncorrectPasswordException;
import com.scaler.backend.userauth.exceptions.InvalidTokenException;
import com.scaler.backend.userauth.exceptions.UserAlreadyExistsException;
import com.scaler.backend.userauth.exceptions.UserNotFoundException;
import com.scaler.backend.userauth.models.Role;
import com.scaler.backend.userauth.models.Session;
import com.scaler.backend.userauth.models.State;
import com.scaler.backend.userauth.models.User;
import com.scaler.backend.userauth.repositories.RoleRepository;
import com.scaler.backend.userauth.repositories.SessionRepository;
import com.scaler.backend.userauth.repositories.UserRepository;
import com.scaler.backend.userauth.utils.JwtUtil;
import com.scaler.backend.userauth.utils.UserDtoMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "NON_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaProducerClient kafkaProducerClient;

    public AuthServiceImpl(UserRepository userRepository,
                            RoleRepository roleRepository,
                            SessionRepository sessionRepository,
                            PasswordEncoder passwordEncoder,
                            JwtUtil jwtUtil,
                            KafkaProducerClient kafkaProducerClient) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.kafkaProducerClient = kafkaProducerClient;
    }

    @Override
    @Transactional
    public UserDto signup(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("A user with email " + email + " already exists");
        }

        Role defaultRole = roleRepository.findByValue(DEFAULT_ROLE)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setValue(DEFAULT_ROLE);
                    return roleRepository.save(role);
                });

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        EmailDto welcomeEmail = new EmailDto(
                savedUser.getEmail(),
                "Welcome to Scaler Backend",
                "Hey " + savedUser.getName() + ", thanks for signing up! Your account is ready to use."
        );
        kafkaProducerClient.publishSignupEvent(welcomeEmail);

        return UserDtoMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user registered with email " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password for " + email);
        }

        String token = jwtUtil.generateToken(user);

        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setExpiresAt(
                LocalDateTime.ofInstant(
                        Instant.now().plusMillis(jwtUtil.getExpirationMs()),
                        ZoneId.systemDefault()
                )
        );
        sessionRepository.save(session);

        return new LoginResponseDto(token, UserDtoMapper.toDto(user));
    }

    @Override
    @Transactional
    public UserDto validateToken(String token) {
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("No session found for the given token"));

        if (session.getState() == State.INACTIVE) {
            throw new InvalidTokenException("This session has already been invalidated");
        }

        try {
            jwtUtil.parseClaims(token);
        } catch (JwtException e) {
            session.setState(State.INACTIVE);
            sessionRepository.save(session);
            throw new InvalidTokenException("Token is invalid or has expired");
        }

        return UserDtoMapper.toDto(session.getUser());
    }

    @Override
    @Transactional
    public void logout(String token) {
        sessionRepository.findByToken(token).ifPresent(session -> {
            session.setState(State.INACTIVE);
            sessionRepository.save(session);
        });
    }
}
