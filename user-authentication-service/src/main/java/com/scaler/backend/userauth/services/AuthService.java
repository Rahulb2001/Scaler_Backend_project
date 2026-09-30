package com.scaler.backend.userauth.services;

import com.scaler.backend.userauth.dtos.LoginResponseDto;
import com.scaler.backend.userauth.dtos.UserDto;

public interface AuthService {

    UserDto signup(String name, String email, String password);

    LoginResponseDto login(String email, String password);

    UserDto validateToken(String token);

    void logout(String token);
}
