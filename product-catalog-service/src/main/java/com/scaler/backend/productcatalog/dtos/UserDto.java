package com.scaler.backend.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Local copy of the shape returned by user-authentication-service's
 * GET /users/{id}. The two services don't share a client library, so this
 * is intentionally a duplicate rather than a shared dependency.
 */
@Getter
@Setter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
}
