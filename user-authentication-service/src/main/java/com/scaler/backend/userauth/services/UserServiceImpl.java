package com.scaler.backend.userauth.services;

import com.scaler.backend.userauth.dtos.UserDto;
import com.scaler.backend.userauth.exceptions.UserAlreadyExistsException;
import com.scaler.backend.userauth.exceptions.UserNotFoundException;
import com.scaler.backend.userauth.models.Role;
import com.scaler.backend.userauth.models.User;
import com.scaler.backend.userauth.repositories.RoleRepository;
import com.scaler.backend.userauth.repositories.UserRepository;
import com.scaler.backend.userauth.utils.UserDtoMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_ROLE = "NON_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with id " + id));
        return UserDtoMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(String name, String email, String password) {
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

        return UserDtoMapper.toDto(userRepository.save(user));
    }
}
