package com.example.minimalhome.service;

import com.example.minimalhome.entity.ScreenTimeLog;
import com.example.minimalhome.entity.ScreenTimeLimit;
import com.example.minimalhome.entity.ScreenTimeCategory;
import com.example.minimalhome.entity.ScreenTimeAppCategory;
import com.example.minimalhome.repository.ScreenTimeRepository;
import com.example.minimalhome.repository.ScreenTimeLimitRepository;
import com.example.minimalhome.repository.ScreenTimeCategoryRepository;
import com.example.minimalhome.repository.ScreenTimeAppCategoryRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ScreenTimeService {

    private final ScreenTimeRepository screenTimeRepository;
    private final ScreenTimeLimitRepository screenTimeLimitRepository;
    private final ScreenTimeCategoryRepository screenTimeCategoryRepository;
    private final ScreenTimeAppCategoryRepository screenTimeAppCategoryRepository;

    public ScreenTimeLog startAppUsage(Long userId, String appName) {
        log.info("Starting app usage tracking for user: {} and app: {}", userId, appName);

        if (screenTimeRepository.existsByUserIdAndAppNameAndIsActiveTrue(userId, appName)) {
            log.warn("Active session already exists for user: {} and app: {}", userId, appName);
            throw new IllegalStateException("Active session already exists for this app");
        }

        ScreenTimeLog screenTimeLog = new ScreenTimeLog();
        screenTimeLog.setUserId(userId);
        screenTimeLog.setAppName(appName);
        screenTimeLog.setStartTime(LocalDateTime.now());
        screenTimeLog.setIsActive(true);

        return screenTimeRepository.save(screenTimeLog);
    }

    public ScreenTimeLog stopAppUsage(Long userId, String appName) {
        log.info("Stopping app usage tracking for user: {} and app: {}", userId, appName);

        List<ScreenTimeLog> activeSessions = screenTimeRepository.findActiveSessionsByUserId(userId);

        ScreenTimeLog activeSession = activeSessions.stream()
                .filter(session -> session.getAppName().equals(appName))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No active session found for user: {} and app: {}", userId, appName);
                    return new IllegalStateException("No active session found for this app");
                });

        activeSession.setEndTime(LocalDateTime.now());
        activeSession.setIsActive(false);

        return screenTimeRepository.save(activeSession);
    }

    public List<ScreenTimeLog> getCurrentActiveSessions(Long userId) {
        log.info("Fetching active sessions for user: {}", userId);

        List<ScreenTimeLog> activeSessions = screenTimeRepository.findActiveSessionsByUserId(userId);

        log.debug("Found {} active sessions for user: {}", activeSessions.size(), userId);
        return activeSessions;
    }

    public ScreenTimeLimit setScreenTimeLimit(Long userId, Long categoryId, Long dailyLimit, Long weeklyLimit) {
        log.info("Setting screen time limits for user: {} and category: {}", userId, categoryId);

        ScreenTimeCategory category = screenTimeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", categoryId);
                    return new IllegalArgumentException("Category not found");
                });

        Optional<ScreenTimeLimit> existingLimit = screenTimeLimitRepository.findByUserIdAndCategory(userId, category);

        if (existingLimit.isPresent()) {
            log.debug("Updating existing limit for user: {} and category: {}", userId, categoryId);
            ScreenTimeLimit limit = existingLimit.get();
            limit.setDailyLimit(dailyLimit);
            limit.setWeeklyLimit(weeklyLimit);
            return screenTimeLimitRepository.save(limit);
        } else {
            log.debug("Creating new limit for user: {} and category: {}", userId, categoryId);
            ScreenTimeLimit newLimit = new ScreenTimeLimit();
            newLimit.setUserId(userId);
            newLimit.setCategory(category);
            newLimit.setDailyLimit(dailyLimit);
            newLimit.setWeeklyLimit(weeklyLimit);
            newLimit.setIsEnabled(true);

            return screenTimeLimitRepository.save(newLimit);
        }
    }

    public Map<String, Long> getDailyScreenTime(Long userId, LocalDate date) {
        log.info("Getting daily screen time for user: {} on date: {}", userId, date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();


        return screenTimeRepository.getAppWiseScreenTime(userId, startOfDay, endOfDay)
                .stream()
                .collect(Collectors.toMap(
                        map -> (String) map.get("appName"),
                        map -> ((Number) map.get("totalDuration")).longValue()
                ));
    }

    public Long getTotalScreenTimeForPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting total screen time for user: {} between {} and {}", userId, startDate, endDate);

        return screenTimeRepository.getTotalScreenTimeForUserInPeriod(userId, startDate, endDate);
    }

    public void assignAppToCategory(String appName, Long categoryId) {
        log.info("Assigning app: {} to category: {}", appName, categoryId);

        ScreenTimeCategory category = screenTimeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (screenTimeAppCategoryRepository.existsByCategoryIdAndAppName(categoryId, appName)) {
            log.warn("App {} is already assigned to category {}", appName, categoryId);
            return;
        }

        ScreenTimeAppCategory appCategory = new ScreenTimeAppCategory();
        appCategory.setCategory(category);
        appCategory.setAppName(appName);

        screenTimeAppCategoryRepository.save(appCategory);
        log.debug("App {} successfully assigned to category {}", appName, categoryId);
    }

    public Optional<ScreenTimeCategory> getAppCategory(String appName) {
        log.info("Getting category for app: {}", appName);

        return screenTimeAppCategoryRepository.findByAppName(appName)
                .map(ScreenTimeAppCategory::getCategory);
    }

    public List<String> getAppsInCategory(Long categoryId) {
        log.info("Getting all apps in category: {}", categoryId);

        return screenTimeAppCategoryRepository.findAppNamesByCategoryId(categoryId);
    }

    public List<ScreenTimeLog> getScreenTimeHistory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching screen time history for user: {} between {} and {}", userId, startDate, endDate);

        return screenTimeRepository.findByUserIdAndStartTimeBetween(userId, startDate, endDate);
    }
}


/*
1. Core screen time tracking methods
- startAppUsage(userId, appName)
   - starts tracking app usage session
   - creates new ScreenTimeLog entry
- stopAppUsage(userId,appName)
   - ends current app usage session
   - calculates duration
- getCurrentActiveSessions(userId)
   - used for monitoring

2. Limit Management Methods
- setScreenTimeLimit(userId, categoryId, dailyLimit, weeklyLimit)
- checkScreenTimeLimit(userId, appName)

3. Stat
- getDailyScreenTime(userId, date)
- getWeeklyScreenTime(userId, weekStart)
- getAppUsageStats(userId, startDate,endDate)
- getCategoryUsageStats(userId, categoryId)

4. category management methods
- assignAppToCategory(appName,categoryId)
- getAppCategory(appName)
 */