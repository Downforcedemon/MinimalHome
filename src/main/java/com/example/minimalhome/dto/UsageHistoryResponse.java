package com.example.minimalhome.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing usage history data")
public class UsageHistoryResponse {

    @Schema(description = "Time period for which history is shown")
    private TimePeriod period;

    @Schema(description = "Total screen time in seconds during the period", example = "86400")
    private Long totalScreenTime;

    @Schema(description = "List of daily usage summaries")
    private List<DailyUsageResponse> dailySummaries;

    @Schema(description = "Aggregated app usage statistics")
    private Map<String, Long> appUsageTotals;

    @Schema(description = "Aggregated category usage statistics")
    private Map<String, Long> categoryUsageTotals;

    @Schema(description = "Usage trends and patterns")
    private Map<String, Object> trends;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimePeriod {
        @Schema(description = "Start date and time of the period")
        private LocalDateTime startDate;

        @Schema(description = "End date and time of the period")
        private LocalDateTime endDate;

        @Schema(description = "Total days in the period", example = "7")
        private Integer totalDays;
    }
}