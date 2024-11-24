package com.example.minimalhome.controller;

import com.example.minimalhome.dto.StartSessionRequest;
import com.example.minimalhome.dto.StopSessionRequest;
import com.example.minimalhome.dto.SessionResponse;
import com.example.minimalhome.dto.ActiveSessionsResponse;
import com.example.minimalhome.dto.DailyUsageResponse;
import com.example.minimalhome.dto.UsageHistoryRequest;
import com.example.minimalhome.dto.UsageHistoryResponse;
import com.example.minimalhome.entity.ScreenTimeLog;
import com.example.minimalhome.service.ScreenTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/screentime")
@Tag(name = "Screen Time", description = "APIs for managing screen time tracking and analytics")
@RequiredArgsConstructor
@Slf4j
public class ScreenTimeController {

    private final ScreenTimeService screenTimeService;

    @PostMapping("/start")
    @Operation(summary = "Start tracking app usage",
            description = "Starts a new screen time tracking session for specified app")
    @ApiResponse(responseCode = "200", description = "Session started successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "409", description = "Active session already exists for this app")
    public ResponseEntity<SessionResponse> startSession(@Valid @RequestBody StartSessionRequest request) {
        log.info("Starting session for user: {} and app: {}", request.getUserId(), request.getAppName());

        try {
            ScreenTimeLog logEntity = screenTimeService.startAppUsage(request.getUserId(), request.getAppName());
            return ResponseEntity.ok(SessionResponse.from(logEntity));
        } catch (IllegalStateException e) {
            log.warn("Failed to start session: {}", e.getMessage());
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            log.error("Error starting session", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/stop")
    @Operation(summary = "Stop tracking app usage",
            description = "Stops an active screen time tracking session for specified app")
    @ApiResponse(responseCode = "200", description = "Session stopped successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    @ApiResponse(responseCode = "404", description = "No active session found for this app")
    public ResponseEntity<SessionResponse> stopSession(@Valid @RequestBody StopSessionRequest request) {
        log.info("Stopping session for user: {} and app: {}", request.getUserId(), request.getAppName());

        try {
            ScreenTimeLog logEntity = screenTimeService.stopAppUsage(request.getUserId(), request.getAppName());
            return ResponseEntity.ok(SessionResponse.from(logEntity));
        } catch (IllegalStateException e) {
            log.error("No active session found for user: {} and app: {}", request.getUserId(), request.getAppName());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            log.error("Error stopping session", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active sessions",
            description = "Retrieves all currently active app usage sessions for the specified user")
    @ApiResponse(responseCode = "200", description = "Active sessions retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user ID")
    public ResponseEntity<ActiveSessionsResponse> getActiveSessions(@RequestParam Long userId) {
        log.info("Fetching active sessions for user: {}", userId);

        try {
            List<ScreenTimeLog> activeLogs = screenTimeService.getCurrentActiveSessions(userId);
            List<SessionResponse> sessionResponses = activeLogs.stream()
                    .map(SessionResponse::from)
                    .toList();
            return ResponseEntity.ok(new ActiveSessionsResponse(sessionResponses));
        } catch (Exception e) {
            log.error("Error fetching active sessions for user: {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/daily")
    @Operation(summary = "Get daily usage summary",
            description = "Retrieves usage summary for a specific date")
    @ApiResponse(responseCode = "200", description = "Daily summary retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid parameters")
    public ResponseEntity<DailyUsageResponse> getDailyUsage(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        log.info("Fetching daily usage for user: {} on date: {}", userId, date);

        try {
            Map<String, Object> usageData = screenTimeService.getDailyScreenTime(userId, date);

            DailyUsageResponse response = new DailyUsageResponse();
            response.setDate(date);
            response.setTotalScreenTime((Long) usageData.get("totalScreenTime"));

            @SuppressWarnings("unchecked")
            Map<String, Long> appBreakdown = (Map<String, Long>) usageData.get("appUsageBreakdown");
            response.setAppUsageBreakdown(appBreakdown);

            @SuppressWarnings("unchecked")
            Map<String, Long> categoryBreakdown = (Map<String, Long>) usageData.get("categoryBreakdown");
            response.setCategoryBreakdown(categoryBreakdown);

            @SuppressWarnings("unchecked")
            List<ScreenTimeLog> sessions = (List<ScreenTimeLog>) usageData.get("sessions");
            if (sessions != null) {
                response.setSessions(sessions.stream()
                        .map(SessionResponse::from)
                        .toList());
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> mostUsedApps = (List<Map<String, Object>>) usageData.get("mostUsedApps");
            response.setMostUsedApps(mostUsedApps);

            response.setProductivityScore((Double) usageData.get("productivityScore"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching daily usage for user: {} on date: {}", userId, date, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/current")
    @Operation(summary = "Get current usage status",
            description = "Retrieves current active sessions and usage statistics")
    @ApiResponse(responseCode = "200", description = "Current status retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid user ID")
    public ResponseEntity<ActiveSessionsResponse> getCurrentUsage(@RequestParam Long userId) {
        log.info("Fetching current usage status for user: {}", userId);

        try {
            List<ScreenTimeLog> currentSessions = screenTimeService.getCurrentActiveSessions(userId);
            List<SessionResponse> sessionResponses = currentSessions.stream()
                    .map(SessionResponse::from)
                    .toList();
            return ResponseEntity.ok(new ActiveSessionsResponse(sessionResponses));
        } catch (Exception e) {
            log.error("Error fetching current usage for user: {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/history")
    @Operation(summary = "Get usage history",
            description = "Retrieves screen time history for specified date range")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public ResponseEntity<UsageHistoryResponse> getUsageHistory(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(required = false) String categoryFilter,
            @RequestParam(required = false) String appNameFilter) {

        log.info("Fetching usage history for user: {} between {} and {}", userId, startDate, endDate);

        try {
            Long totalTime = screenTimeService.getTotalScreenTimeForPeriod(userId, startDate, endDate);

            UsageHistoryResponse response = new UsageHistoryResponse();
            response.setPeriod(new UsageHistoryResponse.TimePeriod(
                    startDate,
                    endDate,
                    startDate.toLocalDate().until(endDate.toLocalDate()).getDays()
            ));
            response.setTotalScreenTime(totalTime);
            response.setDailySummaries(List.of());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error fetching usage history", e);
            return ResponseEntity.badRequest().build();
        }
    }
}

/*
1. POST /api/screentime/start --> start tracking app usuage (userId, appName)     --> requires StartSessionRequest DTO
   - return session details
2. POST /api/screentime/stop --> stop session
   - return Completed session details
3. GET /api/screentime/active (userId)

4. GET /api/screentime/history --> usage history for data range (userId, startDate, endDate)

5. GET /api/screentime/daily --> usage summary for specific date (userId, date)

6. GET /api/screentime/current --> (userId)

Request DTOs:

StartSessionRequest (userId, appName)
StopSessionRequest (userId, appName)
UsageHistoryRequest (startDate, endDate)

Response DTOs:

SessionResponse (id, appName, startTime, duration)
ActiveSessionsResponse (List of active sessions)
DailyUsageResponse (total time, app breakdown)
 */