package com.scaler.backend.userauth.controllers;

import com.scaler.backend.userauth.dtos.SignupRequestDto;
import com.scaler.backend.userauth.dtos.UserDto;
import com.scaler.backend.userauth.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kept separate from AuthController on purpose: this is the "plain CRUD on
 * users" surface that other services (product-catalog-service, for one)
 * talk to over the network, as opposed to the auth flows a real end user
 * would hit directly.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody SignupRequestDto request) {
        UserDto user = userService.createUser(request.getName(), request.getEmail(), request.getPassword());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
