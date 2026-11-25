package com.makers.loans_backend.infrastructure.adapter.in.web.controller;

import com.makers.loans_backend.domain.exception.ResourceNotFoundException;
import com.makers.loans_backend.domain.model.User;
import com.makers.loans_backend.domain.port.out.UserRepositoryPort;
import com.makers.loans_backend.infrastructure.adapter.in.web.dto.AuthResponse;
import com.makers.loans_backend.infrastructure.adapter.in.web.dto.LoginRequest;
import com.makers.loans_backend.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepositoryPort userRepositoryPort,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name()));
    }
}
