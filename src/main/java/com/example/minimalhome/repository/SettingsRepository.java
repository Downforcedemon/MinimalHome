package com.example.minimalhome.repository;

import com.example.minimalhome.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingsRepository extends JpaRepository<AppSetting, Long> {

    List<AppSetting> findByUserId(Long userId);


    Optional<AppSetting> findByUserIdAndKey(Long userId, String key);


    boolean existsByUserIdAndKey(Long userId, String key);


    List<AppSetting> findByUserIdAndKeyStartingWith(Long userId, String keyPrefix);
}