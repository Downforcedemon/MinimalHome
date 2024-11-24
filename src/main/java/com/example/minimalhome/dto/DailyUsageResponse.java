package com.example.minimalhome.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing daily app usage statistics")
public class DailyUsageResponse {

    @Schema(description = "Date for which statistics are shown", example = "2024-11-23")
    private LocalDate date;

    @Schema(description = "Total screen time in seconds", example = "28800")
    private Long totalScreenTime;

    @Schema(description = "Breakdown of time spent per app in seconds",
            example = "{\"Chrome\": 3600, \"VSCode\": 7200}")
    private Map<String, Long> appUsageBreakdown;

    @Schema(description = "Breakdown of time spent per category in seconds",
            example = "{\"Productivity\": 10800, \"Entertainment\": 3600}")
    private Map<String, Long> categoryBreakdown;

    @Schema(description = "List of app usage sessions for the day")
    private List<SessionResponse> sessions;

    @Schema(description = "Most used apps for the day with their categories")
    private List<Map<String, Object>> mostUsedApps;

    @Schema(description = "Productivity score for the day (0-100)", example = "75.5")
    private Double productivityScore;
}