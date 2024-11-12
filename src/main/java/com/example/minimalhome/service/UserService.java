package com.example.minimalhome.service;

import com.example.minimalhome.dto.UserRegistrationRequest;
import com.example.minimalhome.dto.UserResponse;
import com.example.minimalhome.dto.UserUpdateRequest;
import com.example.minimalhome.entity.User;
import com.example.minimalhome.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserResponse> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToUserResponse);
    }

    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUserProfile(Long id, UserUpdateRequest userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if new username is available if it's being changed
        if (userDetails.getUsername() != null &&
                !user.getUsername().equals(userDetails.getUsername()) &&
                userRepository.existsByUsername(userDetails.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        // Check if new email is available if it's being changed
        if (userDetails.getEmail() != null &&
                !user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (userDetails.getUsername() != null) {
            user.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}