package com.example.minimalhome.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing list of all active app usage sessions")
public class ActiveSessionsResponse {

    @Schema(description = "Total number of active sessions", example = "3")
    private int totalActiveSessions;

    @Schema(description = "List of active session details")
    private List<SessionResponse> activeSessions;

    @Schema(description = "Timestamp of when this data was fetched", example = "2024-11-20 14:30:00")
    private String fetchedAt;

    public ActiveSessionsResponse(List<SessionResponse> activeSessions) {
        this.activeSessions = activeSessions;
        this.totalActiveSessions = activeSessions.size();
        this.fetchedAt = java.time.LocalDateTime.now().toString();
    }
}