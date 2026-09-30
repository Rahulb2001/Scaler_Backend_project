package com.scaler.backend.userauth.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private UserDto user;
}
