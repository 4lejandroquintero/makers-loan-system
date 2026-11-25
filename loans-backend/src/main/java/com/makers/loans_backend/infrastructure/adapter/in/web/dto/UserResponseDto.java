package com.makers.loans_backend.infrastructure.adapter.in.web.dto;

import com.makers.loans_backend.domain.model.Role;
import com.makers.loans_backend.domain.model.User;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Role role
) {
    public static UserResponseDto fromDomain(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}