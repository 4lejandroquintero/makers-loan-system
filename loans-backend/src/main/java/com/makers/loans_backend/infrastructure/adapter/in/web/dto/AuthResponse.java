package com.makers.loans_backend.infrastructure.adapter.in.web.dto;

public record AuthResponse(
        String token,
        String email,
        String name,
        String role
) {}
