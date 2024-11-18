package com.example.minimalhome.service;

import com.example.minimalhome.dto.SettingResponse;
import com.example.minimalhome.dto.SettingRequest;
import com.example.minimalhome.dto.UserSettingsResponse;
import com.example.minimalhome.entity.AppSetting;
import com.example.minimalhome.repository.SettingsRepository;
import com.example.minimalhome.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsService {
    private final SettingsRepository settingsRepository;
    private final UserRepository userRepository; // Changed to UserRepository

    @Transactional
    public SettingResponse createSetting(Long userId, SettingRequest request) {
        if (settingsRepository.existsByUserIdAndKey(userId, request.getKey())) {
            throw new IllegalArgumentException("Setting with key '" + request.getKey() + "' already exists");
        }

        // Verify user exists using UserRepository
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create new setting
        AppSetting setting = new AppSetting();
        setting.setUser(user);
        setting.setKey(request.getKey());
        setting.setValue(request.getValue());

        // Save and convert to response
        AppSetting savedSetting = settingsRepository.save(setting);
        return convertToSettingResponse(savedSetting);
    }

    // Helper method to convert Entity to DTO
    private SettingResponse convertToSettingResponse(AppSetting setting) {
        SettingResponse response = new SettingResponse();
        response.setId(setting.getId());
        response.setUserId(setting.getUser().getId());
        response.setKey(setting.getKey());
        response.setValue(setting.getValue());
        response.setCreatedAt(setting.getCreatedAt());
        response.setUpdatedAt(setting.getUpdatedAt());
        return response;
    }

    @Transactional
    public SettingResponse updateSetting(Long userId, String key, SettingRequest request) {
        // Find existing setting
        AppSetting setting = settingsRepository.findByUserIdAndKey(userId, key)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found"));

        // If key in request is different from current key, check for duplicates
        if (!key.equals(request.getKey()) &&
                settingsRepository.existsByUserIdAndKey(userId, request.getKey())) {
            throw new IllegalArgumentException("Setting with key '" + request.getKey() + "' already exists");
        }

        // Update setting
        setting.setKey(request.getKey());
        setting.setValue(request.getValue());

        // Save and convert to response
        AppSetting updatedSetting = settingsRepository.save(setting);
        return convertToSettingResponse(updatedSetting);
    }
    @Transactional(readOnly = true)
    public SettingResponse getSetting(Long userId, String key) {
        return settingsRepository.findByUserIdAndKey(userId, key)
                .map(this::convertToSettingResponse)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found"));
    }

    @Transactional(readOnly = true)
    public UserSettingsResponse getUserSettings(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        List<AppSetting> settings = settingsRepository.findByUserId(userId);
        List<SettingResponse> settingResponses = settings.stream()
                .map(this::convertToSettingResponse)
                .collect(Collectors.toList());

        // Create grouped response
        UserSettingsResponse response = new UserSettingsResponse();
        response.setUserId(userId);
        response.setTotalSettings(settingResponses.size());
        response.setSettings(UserSettingsResponse.organizeByCategory(settingResponses));

        return response;
    }

    @Transactional(readOnly = true)
    public List<SettingResponse> getSettingsByKeyPrefix(Long userId, String keyPrefix) {
        return settingsRepository.findByUserIdAndKeyStartingWith(userId, keyPrefix)
                .stream()
                .map(this::convertToSettingResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSetting(Long userId, String key) {
        AppSetting setting = settingsRepository.findByUserIdAndKey(userId, key)
                .orElseThrow(() -> new IllegalArgumentException("Setting not found"));

        settingsRepository.delete(setting);
    }

    @Transactional
    public void deleteAllUserSettings(Long userId) {
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        List<AppSetting> settings = settingsRepository.findByUserId(userId);
        settingsRepository.deleteAll(settings);
    }

    // Batch operations
    @Transactional
    public List<SettingResponse> createSettings(Long userId, List<SettingRequest> requests) {
        return requests.stream()
                .map(request -> createSetting(userId, request))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<SettingResponse> updateSettings(Long userId, List<SettingRequest> requests) {
        return requests.stream()
                .map(request -> {
                    AppSetting existing = settingsRepository.findByUserIdAndKey(userId, request.getKey())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Setting with key '" + request.getKey() + "' not found"));

                    existing.setValue(request.getValue());
                    return convertToSettingResponse(settingsRepository.save(existing));
                })
                .collect(Collectors.toList());
    }

    // Helper method for checking if setting exists
    public boolean existsSetting(Long userId, String key) {
        return settingsRepository.existsByUserIdAndKey(userId, key);
    }


}

