package com.example.minimalhome.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name  = "screen_time_logs")
public class ScreenTimeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "app_name", nullable = false)
    private String appName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        if (endTime != null && duration == null) {
            duration = java.time.Duration.between(startTime, endTime).toSeconds();
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        if (this.startTime != null) {
            this.duration = java.time.Duration.between(startTime, endTime).toSeconds();
        }
    }
}

