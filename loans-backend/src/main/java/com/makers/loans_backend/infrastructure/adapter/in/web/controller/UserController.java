package com.makers.loans_backend.infrastructure.adapter.in.web.controller;

import com.makers.loans_backend.domain.model.Role;
import com.makers.loans_backend.domain.model.User;
import com.makers.loans_backend.domain.port.in.UserUseCase;
import com.makers.loans_backend.infrastructure.adapter.in.web.dto.UserRequestDto;
import com.makers.loans_backend.infrastructure.adapter.in.web.dto.UserResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto request) {
        User user = new User(
                null,
                request.name(),
                request.email(),
                request.password(),
                request.role() != null ? request.role() : Role.ROLE_USER
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResponseDto.fromDomain(userUseCase.createUser(user)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        var users = userUseCase.getAllUsers().stream().map(UserResponseDto::fromDomain).toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponseDto.fromDomain(userUseCase.getUserById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserRequestDto request) {
        User user = new User(id, request.name(), request.email(), request.password(), request.role());
        return ResponseEntity.ok(UserResponseDto.fromDomain(userUseCase.updateUser(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
