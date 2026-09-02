package com.svsbas.modules.user.controller;

import com.svsbas.common.payload.ApiResponse;
import com.svsbas.modules.auth.dto.RegisterRequest;
import com.svsbas.modules.user.dto.UserResponse;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User profile and fleet personnel management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        UserResponse response = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all registered accounts (Admin only)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) Role role) {
        List<UserResponse> list = (role != null) ? userService.getUsersByRole(role) : userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/mechanic")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Provision a certified mechanic account (Admin only)")
    public ResponseEntity<ApiResponse<UserResponse>> createMechanic(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.createMechanic(request);
        return new ResponseEntity<>(ApiResponse.success("Mechanic account created", response), HttpStatus.CREATED);
    }
}
