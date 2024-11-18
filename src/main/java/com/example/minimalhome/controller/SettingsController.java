package com.example.minimalhome.controller;

import com.example.minimalhome.dto.SettingRequest;
import com.example.minimalhome.dto.SettingResponse;
import com.example.minimalhome.dto.UserSettingsResponse;
import com.example.minimalhome.service.SettingsService;
import com.example.minimalhome.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingsService;
    private final JWTUtil jwtUtil;

    // Utility method to get userId
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(token);
    }

    @GetMapping
    public ResponseEntity<UserSettingsResponse> getUserSettings(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.getUserSettings(userId));
    }

    @GetMapping("/{key}")
    public ResponseEntity<SettingResponse> getSetting(
            HttpServletRequest request,
            @PathVariable String key) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.getSetting(userId, key));
    }

    @GetMapping("/category/{prefix}")
    public ResponseEntity<List<SettingResponse>> getSettingsByCategory(
            HttpServletRequest request,
            @PathVariable String prefix) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.getSettingsByKeyPrefix(userId, prefix));
    }

    @PostMapping
    public ResponseEntity<SettingResponse> createSetting(
            HttpServletRequest request,
            @Valid @RequestBody SettingRequest settingRequest) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.createSetting(userId, settingRequest));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<SettingResponse>> createSettings(
            HttpServletRequest request,
            @Valid @RequestBody List<SettingRequest> settingRequests) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.createSettings(userId, settingRequests));
    }

    @PutMapping("/{key}")
    public ResponseEntity<SettingResponse> updateSetting(
            HttpServletRequest request,
            @PathVariable String key,
            @Valid @RequestBody SettingRequest settingRequest) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.updateSetting(userId, key, settingRequest));
    }

    @PutMapping("/batch")
    public ResponseEntity<List<SettingResponse>> updateSettings(
            HttpServletRequest request,
            @Valid @RequestBody List<SettingRequest> settingRequests) {
        Long userId = getCurrentUserId(request);
        return ResponseEntity.ok(settingsService.updateSettings(userId, settingRequests));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSetting(
            HttpServletRequest request,
            @PathVariable String key) {
        Long userId = getCurrentUserId(request);
        settingsService.deleteSetting(userId, key);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSettings(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        settingsService.deleteAllUserSettings(userId);
        return ResponseEntity.noContent().build();
    }
}

/*
Postman Collection Documentation for Settings API

1. GET /api/settings
Headers:
  Authorization: Bearer {jwt_token}
Response:
{
    "userId": 1,
    "totalSettings": 2,
    "settings": {
        "notification": [
            {
                "key": "notification.email",
                "value": "true",
                "createdAt": "2024-01-01T10:00:00"
            }
        ],
        "theme": [
            {
                "key": "theme.darkMode",
                "value": "true",
                "createdAt": "2024-01-01T10:00:00"
            }
        ]
    }
}

2. POST /api/settings
Headers:
  Authorization: Bearer {jwt_token}
  Content-Type: application/json
Body:
{
    "key": "notification.email",
    "value": "true"
}
Response:
{
    "id": 1,
    "userId": 1,
    "key": "notification.email",
    "value": "true",
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": null
}

3. PUT /api/settings/{key}
Headers:
  Authorization: Bearer {jwt_token}
  Content-Type: application/json
Body:
{
    "key": "notification.email",
    "value": "false"
}

4. POST /api/settings/batch
Headers:
  Authorization: Bearer {jwt_token}
  Content-Type: application/json
Body:
[
    {
        "key": "notification.email",
        "value": "true"
    },
    {
        "key": "notification.sms",
        "value": "false"
    }
]

5. GET /api/settings/category/notification
Headers:
  Authorization: Bearer {jwt_token}
Response:
[
    {
        "id": 1,
        "userId": 1,
        "key": "notification.email",
        "value": "true",
        "createdAt": "2024-01-01T10:00:00"
    },
    {
        "id": 2,
        "userId": 1,
        "key": "notification.sms",
        "value": "false",
        "createdAt": "2024-01-01T10:00:00"
    }
]

Common Error Responses:
400 Bad Request:
{
    "error": "Invalid request",
    "message": "Setting key is required"
}

404 Not Found:
{
    "error": "Not found",
    "message": "Setting not found"
}

401 Unauthorized:
{
    "error": "Unauthorized",
    "message": "Invalid or expired token"
}
*/