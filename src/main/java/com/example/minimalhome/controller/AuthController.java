package com.example.minimalhome.controller;

import com.example.minimalhome.dto.AuthResponse;
import com.example.minimalhome.dto.UserLoginRequest;
import com.example.minimalhome.dto.UserRegistrationRequest;
import com.example.minimalhome.dto.UserResponse;
import com.example.minimalhome.service.UserAuthService;
import com.example.minimalhome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserAuthService userAuthService;

    public AuthController(UserService userService, UserAuthService userAuthService) {
        this.userService = userService;
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.registerUser(request);

        AuthResponse<UserResponse> response = new AuthResponse<>();
        response.setSuccess("true");
        response.setMessage("User registered successfully");
        response.setData(userResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse<Map<String, String>>> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        String token = userAuthService.authenticateUser(loginRequest);

        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", token);
        tokenData.put("username", loginRequest.getUsername());

        AuthResponse<Map<String, String>> response = new AuthResponse<>();
        response.setSuccess("true");
        response.setMessage("Login successful");
        response.setData(tokenData);

        return ResponseEntity.ok(response);
    }
}