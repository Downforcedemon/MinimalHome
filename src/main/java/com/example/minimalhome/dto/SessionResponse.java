package com.example.minimalhome.dto;

import com.example.minimalhome.entity.ScreenTimeLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String appName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long duration;
    private Boolean isActive;

    public static SessionResponse from(ScreenTimeLog log) {
        SessionResponse response = new SessionResponse();
        response.setId(log.getId());
        response.setAppName(log.getAppName());
        response.setStartTime(log.getStartTime());
        response.setEndTime(log.getEndTime());
        response.setDuration(log.getDuration());
        response.setIsActive(log.getIsActive());
        return response;
    }
}