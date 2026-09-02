package com.svsbas.modules.auth.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.auth.dto.LoginRequest;
import com.svsbas.modules.auth.dto.LoginResponse;
import com.svsbas.modules.auth.dto.RegisterRequest;
import com.svsbas.modules.auth.service.AuthService;
import com.svsbas.modules.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and JWT token authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT bearer token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new customer account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(ApiResponse.success("Account created successfully", response), HttpStatus.CREATED);
    }
}
