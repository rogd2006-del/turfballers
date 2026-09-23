package com.turfballers.backend.service;

import com.turfballers.backend.config.JwtService;
import com.turfballers.backend.dto.*;
import com.turfballers.backend.exception.ResourceNotFoundException;
import com.turfballers.backend.model.User;
import com.turfballers.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service: login, registration, profile management.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /** Authenticate admin and return JWT token */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        return buildResponse(token, user, "Login successful");
    }

    /** Register a new admin */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }
        String name = (request.getFullName() != null && !request.getFullName().isBlank())
            ? request.getFullName() : "Administrator";
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(name)
            .role("ADMIN")
            .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return buildResponse(token, user, "Registration successful");
    }

    /** Get admin profile by email */
    public AuthResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildResponse(null, user, "Profile retrieved");
    }

    /** Update admin profile details */
    public AuthResponse updateProfile(String email, ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);
        return buildResponse(null, user, "Profile updated successfully");
    }

    /** Change admin password */
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private AuthResponse buildResponse(String token, User user, String message) {
        return AuthResponse.builder()
            .token(token)
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(user.getRole())
            .avatarUrl(user.getAvatarUrl())
            .message(message)
            .build();
    }
}
