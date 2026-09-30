package com.scaler.backend.userauth.utils;

import com.scaler.backend.userauth.dtos.UserDto;
import com.scaler.backend.userauth.models.Role;
import com.scaler.backend.userauth.models.User;

import java.util.List;

public class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream().map(Role::getValue).toList());
        return dto;
    }
}
