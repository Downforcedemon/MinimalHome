package com.example.minimalhome.repository;

import com.example.minimalhome.entity.ScreenTimeLimit;
import com.example.minimalhome.entity.ScreenTimeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenTimeLimitRepository extends JpaRepository<ScreenTimeLimit, Long> {

    Optional<ScreenTimeLimit> findByUserIdAndCategory(Long userId, ScreenTimeCategory category);

    @Query("SELECT l FROM ScreenTimeLimit l LEFT JOIN FETCH l.category WHERE l.userId = :userId AND l.isEnabled = true")
    List<ScreenTimeLimit> findActiveByUserId(@Param("userId") Long userId);

    Optional<ScreenTimeLimit> findByUserIdAndCategoryAndIsEnabledTrue(Long userId, ScreenTimeCategory category);

    @Modifying
    @Query("UPDATE ScreenTimeLimit l SET l.dailyLimit = :dailyLimit, l.weeklyLimit = :weeklyLimit " +
            "WHERE l.userId = :userId AND l.category = :category")
    int updateLimit(@Param("userId") Long userId,
                    @Param("category") ScreenTimeCategory category,
                    @Param("dailyLimit") Long dailyLimit,
                    @Param("weeklyLimit") Long weeklyLimit);

    @Modifying
    @Query("DELETE FROM ScreenTimeLimit l WHERE l.userId = :userId AND l.category = :category")
    void deleteByUserIdAndCategory(@Param("userId") Long userId, @Param("category") ScreenTimeCategory category);

    boolean existsByUserIdAndCategory(Long userId, ScreenTimeCategory category);
}



/*
findByUserIdANDCategory --> limit settings for user and category
findActiveByUserId --> all active limits by userId
findByUserIdAndCategoryAndIsEnabledTrue --> active limit for specific category
updateLimit
deleteByUserIdAndCategory
existsByUserIdAndCategory
 */