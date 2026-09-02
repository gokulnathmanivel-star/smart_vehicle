package com.svsbas.modules.user.service;

import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.modules.auth.dto.RegisterRequest;
import com.svsbas.modules.auth.service.AuthService;
import com.svsbas.modules.user.dto.UserResponse;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return new UserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse createMechanic(RegisterRequest request) {
        request.setRole(Role.ROLE_MECHANIC);
        return authService.register(request);
    }
}
