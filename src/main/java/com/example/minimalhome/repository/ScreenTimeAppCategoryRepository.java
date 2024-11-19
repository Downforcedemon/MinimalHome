package com.example.minimalhome.repository;

import com.example.minimalhome.entity.ScreenTimeAppCategory;
import com.example.minimalhome.entity.ScreenTimeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenTimeAppCategoryRepository extends JpaRepository<ScreenTimeAppCategory, Long> {

    List<ScreenTimeAppCategory> findByCategoryId(Long categoryId);

    Optional<ScreenTimeAppCategory> findByAppName(String appName);

    Optional<ScreenTimeAppCategory> findByCategoryIdAndAppName(Long categoryId, String appName);

    boolean existsByCategoryIdAndAppName(Long categoryId, String appName);

    void deleteByCategoryIdAndAppName(Long categoryId, String appName);

    @Query("SELECT COUNT(a) FROM ScreenTimeAppCategory a WHERE a.category.id = :categoryId")
    Long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT DISTINCT a.appName FROM ScreenTimeAppCategory a WHERE a.category.id = :categoryId")
    List<String> findAppNamesByCategoryId(@Param("categoryId") Long categoryId);
}


/*
findByCategoryId
findByAppName
findByCategoryIdAndAppName
existsByCategoryIdAndAppName
deleteByCategoryIdAndAppName
countByCategoryId
 */