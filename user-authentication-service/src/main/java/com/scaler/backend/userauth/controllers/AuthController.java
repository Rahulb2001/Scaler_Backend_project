package com.scaler.backend.userauth.controllers;

import com.scaler.backend.userauth.dtos.LoginRequestDto;
import com.scaler.backend.userauth.dtos.LoginResponseDto;
import com.scaler.backend.userauth.dtos.SignupRequestDto;
import com.scaler.backend.userauth.dtos.UserDto;
import com.scaler.backend.userauth.dtos.ValidateTokenRequestDto;
import com.scaler.backend.userauth.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto request) {
        UserDto user = authService.signup(request.getName(), request.getEmail(), request.getPassword());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResponseDto loginResponse = authService.login(request.getEmail(), request.getPassword());

        Cookie cookie = new Cookie("auth-token", loginResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<UserDto> validateToken(@RequestBody ValidateTokenRequestDto request) {
        return ResponseEntity.ok(authService.validateToken(request.getToken()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        return ResponseEntity.ok((UserDto) authentication.getPrincipal());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.substring(7)
                : authorizationHeader;
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
