package com.example.minimalhome.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SettingRequest {
    @NotBlank(message = "Setting key is required")
    @Size(min = 2, max = 255, message = "Setting key must be between 2 and 255 characters")
    @Pattern(regexp = "^[a-z0-9.]*$", message = "Setting key can only contain lowercase letters, numbers, and dots")
    private String key;

    @NotBlank(message = "Setting value is required")
    @Size(max = 1000, message = "Setting value cannot exceed 1000 characters")
    private String value;
}

