package com.svsbas.modules.auth.service;

import com.svsbas.common.exception.BadRequestException;
import com.svsbas.modules.auth.dto.LoginRequest;
import com.svsbas.modules.auth.dto.LoginResponse;
import com.svsbas.modules.auth.dto.RegisterRequest;
import com.svsbas.modules.user.dto.UserResponse;
import com.svsbas.modules.user.entity.Role;
import com.svsbas.modules.user.entity.User;
import com.svsbas.modules.user.repository.UserRepository;
import com.svsbas.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("User profile not found"));

        return new LoginResponse(jwt, new UserResponse(user));
    }

    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("An account is already registered with email: " + registerRequest.getEmail());
        }

        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new BadRequestException("An account is already registered with phone number: " + registerRequest.getPhone());
        }

        Role assignedRole = registerRequest.getRole() != null ? registerRequest.getRole() : Role.ROLE_CUSTOMER;

        User user = new User(
                registerRequest.getEmail(),
                registerRequest.getPhone(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFullName(),
                assignedRole
        );

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser);
    }
}
