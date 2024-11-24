package com.example.minimalhome.repository;

import com.example.minimalhome.entity.ScreenTimeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenTimeCategoryRepository extends JpaRepository<ScreenTimeCategory, Long> {

    Optional<ScreenTimeCategory> findByName(String name);

    List<ScreenTimeCategory> findByNameContainingIgnoreCase(String namePart);

    @Query("SELECT c FROM ScreenTimeCategory c LEFT JOIN FETCH c.apps WHERE c.id = :categoryId")
    Optional<ScreenTimeCategory> findWithApps(@Param("categoryId") Long categoryId);

    boolean existsByName(String name);

    @Query("SELECT DISTINCT c FROM ScreenTimeCategory  c " +
            "INNER JOIN ScreenTimeLimit  l ON l.category = c " +
            "WHERE l.userId = :userId AND l.isEnabled = true")
    List<ScreenTimeCategory> findCategoriesWithActiveLimits(@Param("userId") Long userId);

}







/*
findByName --> useful to look up categories like "Social,"Gaming"
findByNameContainingIgnoreCase --> case-insensitive search with partial name
findWithApps -->
existsByName --> useful before creating new category
findCategoriesWithActiveLimits  --> active time limits
*/