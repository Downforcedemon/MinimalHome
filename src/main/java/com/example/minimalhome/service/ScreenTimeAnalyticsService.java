package com.example.minimalhome.service;

import com.example.minimalhome.entity.ScreenTimeAppCategory;
import com.example.minimalhome.entity.ScreenTimeLog;
import com.example.minimalhome.entity.ScreenTimeCategory;
import com.example.minimalhome.repository.ScreenTimeRepository;
import com.example.minimalhome.repository.ScreenTimeCategoryRepository;
import com.example.minimalhome.repository.ScreenTimeAppCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScreenTimeAnalyticsService {

    private final ScreenTimeRepository screenTimeRepository;
    private final ScreenTimeCategoryRepository screenTimeCategoryRepository;
    private final ScreenTimeAppCategoryRepository screenTimeAppCategoryRepository;

    public Map<String, Object> calculateDailyStats(Long userId, LocalDate date) {
        log.info("Calculating daily stats for user: {} on date: {}", userId, date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Map<String, Object> stats = new HashMap<>();

        // Total screen time
        Long totalTime = screenTimeRepository.getTotalScreenTimeForUserInPeriod(userId, startOfDay, endOfDay);
        stats.put("totalScreenTime", totalTime != null ? totalTime : 0L);

        // App-wise breakdown
        List<Map<String, Object>> appWiseTime = screenTimeRepository.getAppWiseScreenTime(userId, startOfDay, endOfDay);
        stats.put("appWiseBreakdown", appWiseTime);

        // Category-wise breakdown
        Map<String, Long> categoryWiseTime = calculateCategoryWiseTime(appWiseTime);
        stats.put("categoryWiseBreakdown", categoryWiseTime);

        return stats;
    }

    public Map<String, Object> calculateWeeklyStats(Long userId, LocalDate startDate) {
        log.info("Calculating weekly stats for user: {} starting from: {}", userId, startDate);

        LocalDateTime weekStart = startDate.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime weekEnd = weekStart.plusWeeks(1);

        Map<String, Object> weeklyStats = new HashMap<>();

        // Daily breakdown for the week
        Map<LocalDate, Long> dailyBreakdown = new HashMap<>();
        for (LocalDate date = startDate; date.isBefore(startDate.plusWeeks(1)); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            Long dailyTotal = screenTimeRepository.getTotalScreenTimeForUserInPeriod(userId, dayStart, dayEnd);
            dailyBreakdown.put(date, dailyTotal != null ? dailyTotal : 0L);
        }
        weeklyStats.put("dailyBreakdown", dailyBreakdown);

        // Weekly totals
        Long weeklyTotal = screenTimeRepository.getTotalScreenTimeForUserInPeriod(userId, weekStart, weekEnd);
        weeklyStats.put("totalWeeklyTime", weeklyTotal != null ? weeklyTotal : 0L);

        // Most used apps
        weeklyStats.put("mostUsedApps", getMostUsedApps(userId, weekStart, weekEnd, 5));

        return weeklyStats;
    }

    public List<Map<String, Object>> generateUsageTrends(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating usage trends for user: {} between {} and {}", userId, startDate, endDate);

        List<Map<String, Object>> trends = new ArrayList<>();

        // App usage frequency
        List<Map<String, Object>> appUsage = screenTimeRepository.getAppWiseScreenTime(userId, startDate, endDate);
        trends.add(Map.of("type", "appUsage", "data", appUsage));

        // Daily patterns
        Map<String, Long> dailyPatterns = calculateDailyPatterns(userId, startDate, endDate);
        trends.add(Map.of("type", "dailyPatterns", "data", dailyPatterns));

        return trends;
    }

    public List<Map<String, Object>> getMostUsedApps(Long userId, LocalDateTime startDate, LocalDateTime endDate, int limit) {
        log.info("Getting most used apps for user: {} between {} and {}", userId, startDate, endDate);

        return screenTimeRepository.getAppWiseScreenTime(userId, startDate, endDate).stream()
                .sorted((a, b) -> ((Long) b.get("totalDuration")).compareTo((Long) a.get("totalDuration")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public double calculateProductivityScore(Long userId, LocalDate date) {
        log.info("Calculating productivity score for user: {} on date: {}", userId, date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Map<String, Object>> appUsage = screenTimeRepository.getAppWiseScreenTime(userId, startOfDay, endOfDay);

        double productiveTime = 0.0;
        double totalTime = 0.0;

        for (Map<String, Object> usage : appUsage) {
            String appName = (String) usage.get("appName");
            Long duration = ((Number) usage.get("totalDuration")).longValue();
            totalTime += duration;

            // Check if app is in productive category
            Optional<ScreenTimeCategory> category = screenTimeAppCategoryRepository
                    .findByAppName(appName)
                    .map(ScreenTimeAppCategory::getCategory);

            if (category.isPresent() && category.get().getName().equalsIgnoreCase("Productivity")) {
                productiveTime += duration;
            }
        }

        return totalTime > 0 ? (productiveTime / totalTime) * 100 : 0.0;
    }

    private Map<String, Long> calculateCategoryWiseTime(List<Map<String, Object>> appWiseTime) {
        Map<String, Long> categoryTime = new HashMap<>();

        for (Map<String, Object> appUsage : appWiseTime) {
            String appName = (String) appUsage.get("appName");
            Long duration = ((Number) appUsage.get("totalDuration")).longValue();

            screenTimeAppCategoryRepository.findByAppName(appName)
                    .ifPresent(appCategory -> {
                        String categoryName = appCategory.getCategory().getName();
                        categoryTime.merge(categoryName, duration, Long::sum);
                    });
        }

        return categoryTime;
    }

    private Map<String, Long> calculateDailyPatterns(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation for calculating daily usage patterns
        return new HashMap<>(); // Placeholder
    }
}

/*
calculateDailyStats(userId, date)
calculateWeeklyStats(userId, startDate)
generateUsageTrends(userId, startDate, endDate)
getMostUsedApps(userId, timeRange)
getProductivityScore(userId)
generateUserReport(userId, timeRange)
 */