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
import com.example.minimalhome.service.ScreenTimeAnalyticsService;
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

@RestController
@RequestMapping("/api/screentime")
@Tag(name = "Screen Time", description = "APIs for managing screen time tracking and analytics")
@RequiredArgsConstructor
@Slf4j
public class ScreenTimeController {

    private final ScreenTimeService screenTimeService;
    private final ScreenTimeAnalyticsService screenTimeAnalyticsService;

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