package com.example.minimalhome.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for stopping an active app usage session")
public class StopSessionRequest {

    @NotNull(message = "User ID cannot be null")
    @Schema(description = "ID of the user stopping the session", example = "1")
    private Long userId;

    @NotBlank(message = "App name cannot be empty")
    @Size(min = 1, max = 100, message = "App name must be between 1 and 100 characters")
    @Schema(description = "Name of the app to stop tracking", example = "Chrome")
    private String appName;
}