/* handle core screen time logging operations */


package com.example.minimalhome.repository;

import com.example.minimalhome.entity.ScreenTimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@SuppressWarnings("unused")                    /* remove later */
public interface ScreenTimeRepository extends JpaRepository<ScreenTimeLog, Long> {

    List<ScreenTimeLog> findByUserIdAndStartTimeBetween(
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT s FROM ScreenTimeLog s WHERE s.userId = :userId " +
            "AND s.isActive = true")
    List<ScreenTimeLog> findActiveSessionsByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndAppNameAndIsActiveTrue(Long userId, String appName);

    @Query("SELECT SUM(s.duration) FROM ScreenTimeLog s " +
            "WHERE s.userId = :userId AND s.startTime BETWEEN :startDate AND :endDate")
    Long getTotalScreenTimeForUserInPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s.appName as appName, SUM(s.duration) as totalDuration " +
            "FROM ScreenTimeLog s " +
            "WHERE s.userId = :userId AND s.startTime BETWEEN :startDate AND :endDate " +
            "GROUP BY s.appName")
    List<Map<String, Object>> getAppWiseScreenTime(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM ScreenTimeLog s " +
            "WHERE s.userId = :userId AND s.appName = :appName " +
            "AND s.startTime BETWEEN :startDate AND :endDate")
    Long getAppOpenCount(
            @Param("userId") Long userId,
            @Param("appName") String appName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}