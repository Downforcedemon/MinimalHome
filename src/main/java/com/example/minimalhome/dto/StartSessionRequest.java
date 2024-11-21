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
@Schema(description = "Request objec for starting a new app usage session")
public class StartSessionRequest {

    @NotNull(message = "User id cannot be null")
    @Schema(description = "Id of the user starting the session", example = "1")
    private Long userId;

    @NotBlank(message = "App name cannot be empty")
    @Size(min = 1, max = 25, message = "App name must be between 1 and 25 char")
    @Schema(description = "name of the app being used", example = "Chrome")
    private String appName;
}
