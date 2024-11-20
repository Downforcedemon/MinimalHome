package com.example.minimalhome.service;

import com.example.minimalhome.entity.*;
import com.example.minimalhome.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ScreenTimeNotificationService {

    private final ScreenTimeRepository screenTimeRepository;
    private final ScreenTimeLimitRepository screenTimeLimitRepository;
    private final ScreenTimeAppCategoryRepository screenTimeAppCategoryRepository;
    private final ScreenTimeService screenTimeService;

    public boolean checkLimitExceeded(Long userId, String appName) {
        log.info("Checking limit for user: {} and app: {}", userId, appName);

        // Get app's category
        Optional<ScreenTimeCategory> categoryOpt = screenTimeService.getAppCategory(appName);
        if (categoryOpt.isEmpty()) {
            log.debug("No category found for app: {}", appName);
            return false;
        }

        // Get category limits
        Optional<ScreenTimeLimit> limitOpt = screenTimeLimitRepository
                .findByUserIdAndCategoryAndIsEnabledTrue(userId, categoryOpt.get());

        if (limitOpt.isEmpty()) {
            log.debug("No limits set for category: {}", categoryOpt.get().getName());
            return false;
        }

        ScreenTimeLimit limit = limitOpt.get();

        // Check daily limit
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        Long dailyUsage = screenTimeRepository.getTotalScreenTimeForUserInPeriod(
                userId, startOfDay, LocalDateTime.now());

        if (dailyUsage != null && dailyUsage >= limit.getDailyLimit()) {
            log.info("Daily limit exceeded for user: {} in category: {}", userId, categoryOpt.get().getName());
            return true;
        }

        // Check weekly limit
        LocalDateTime startOfWeek = LocalDate.now().minusDays(6).atStartOfDay();
        Long weeklyUsage = screenTimeRepository.getTotalScreenTimeForUserInPeriod(
                userId, startOfWeek, LocalDateTime.now());

        if (weeklyUsage != null && weeklyUsage >= limit.getWeeklyLimit()) {
            log.info("Weekly limit exceeded for user: {} in category: {}", userId, categoryOpt.get().getName());
            return true;
        }

        return false;
    }

    public void sendLimitWarning(Long userId, ScreenTimeCategory category) {
        log.info("Sending limit warning for user: {} and category: {}", userId, category.getName());

        // Implementation would connect to notification system
        // This is a placeholder for actual notification logic
        String message = String.format("You have reached your screen time limit for %s", category.getName());
        log.info("Warning message: {}", message);
    }

    public Map<String, Object> createDailyDigest(Long userId) {
        log.info("Creating daily digest for user: {}", userId);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        // Get total usage
        Long totalTime = screenTimeRepository.getTotalScreenTimeForUserInPeriod(userId, startOfDay, endOfDay);

        // Get app breakdown
        List<Map<String, Object>> appBreakdown = screenTimeRepository.getAppWiseScreenTime(
                userId, startOfDay, endOfDay);

        return Map.of(
                "totalTime", totalTime != null ? totalTime : 0L,
                "appBreakdown", appBreakdown,
                "timestamp", LocalDateTime.now()
        );
    }

    public void scheduleReminders(Long userId) {
        log.info("Scheduling reminders for user: {}", userId);
        // Implementation would handle reminder scheduling
        // This is a placeholder for actual scheduling logic
    }

    public void processNotification(Long userId, String notificationType) {
        log.info("Processing notification type: {} for user: {}", notificationType, userId);

        switch (notificationType) {
            case "LIMIT_WARNING":
                // Process limit warning
                break;
            case "DAILY_DIGEST":
                createDailyDigest(userId);
                break;
            case "REMINDER":
                scheduleReminders(userId);
                break;
            default:
                log.warn("Unknown notification type: {}", notificationType);
        }
    }
}

/*
checkLimitExceeded(userId, appName)
sendLimitWarning(userId, category)
createDailyDigest(userId)
scheduleReminders(userId)
processNotification(userId, notificationType)
markNotificationAsRead(notificationId)
 */