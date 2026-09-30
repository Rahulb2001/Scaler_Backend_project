package com.scaler.backend.userauth.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateTokenRequestDto {
    private String token;
}
