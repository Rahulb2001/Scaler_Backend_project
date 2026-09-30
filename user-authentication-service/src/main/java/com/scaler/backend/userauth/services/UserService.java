package com.scaler.backend.userauth.services;

import com.scaler.backend.userauth.dtos.UserDto;

public interface UserService {

    UserDto getUserById(Long id);

    UserDto createUser(String name, String email, String password);
}
