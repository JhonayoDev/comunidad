package com.space.comunidad.domain.user.dto;

import com.space.comunidad.domain.user.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String nombre,
    @NotBlank @Email String email,
    @NotBlank String password,
    @NotBlank Role role) {
}
