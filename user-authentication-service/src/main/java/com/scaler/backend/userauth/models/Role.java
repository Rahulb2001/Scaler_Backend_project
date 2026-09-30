package com.scaler.backend.userauth.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends BaseModel {

    @Column(nullable = false, unique = true)
    private String value;
}
