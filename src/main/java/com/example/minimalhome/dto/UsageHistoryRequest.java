package com.example.minimalhome.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for fetching usage history within a date range")
public class UsageHistoryRequest {

    @NotNull(message = "User ID cannot be null")
    @Schema(description = "ID of the user requesting history", example = "1")
    private Long userId;

    @NotNull(message = "Start date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Start date and time for history search",
            example = "2024-11-20 00:00:00",
            pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "End date and time for history search",
            example = "2024-11-20 23:59:59",
            pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "Category filter (optional)", example = "Productivity")
    private String categoryFilter;

    @Schema(description = "App name filter (optional)", example = "Chrome")
    private String appNameFilter;
}