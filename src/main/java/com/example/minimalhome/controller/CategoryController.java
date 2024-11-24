package com.example.minimalhome.controller;

import com.example.minimalhome.dto.CategoryAssignmentResponse;
import com.example.minimalhome.entity.ScreenTimeCategory;
import com.example.minimalhome.service.ScreenTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screentime")
@Tag(name = "App Categories", description = "APIs for managing app categories and assignments")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final ScreenTimeService screenTimeService;

    @PostMapping("/categories")
    @Operation(summary = "Create a new app category")
    @ApiResponse(responseCode = "200", description = "Category created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request or category exists")
    public ResponseEntity<ScreenTimeCategory> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        try {
            ScreenTimeCategory category = screenTimeService.createCategory(request.getName(), request.getDescription());
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create category: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/categories/{categoryId}/apps")
    @Operation(summary = "Assign app to category")
    @ApiResponse(responseCode = "200", description = "App assigned to category successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public ResponseEntity<CategoryAssignmentResponse> assignAppToCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody AppAssignmentRequest request) {
        log.info("Assigning app: {} to category: {}", request.getAppName(), categoryId);
        try {
            ScreenTimeCategory category = screenTimeService.assignAppToCategoryWithResponse(request.getAppName(), categoryId);
            return ResponseEntity.ok(CategoryAssignmentResponse.builder()
                    .success(true)
                    .appName(request.getAppName())
                    .categoryId(categoryId)
                    .categoryName(category.getName())
                    .message(String.format("App '%s' has been successfully assigned to '%s' category",
                            request.getAppName(), category.getName()))
                    .build());
        } catch (IllegalArgumentException e) {
            log.warn("Failed to assign app to category: {}", e.getMessage());
            return ResponseEntity.ok(CategoryAssignmentResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @GetMapping("/apps/{appName}")
    @Operation(summary = "Get category for an app")
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    @ApiResponse(responseCode = "404", description = "App category not found")
    public ResponseEntity<CategoryAssignmentResponse> getAppCategory(@PathVariable String appName) {
        log.info("Getting category for app: {}", appName);
        return screenTimeService.getAppCategory(appName)
                .map(CategoryAssignmentResponse::fromCategory)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories/{categoryId}/apps")
    @Operation(summary = "Get all apps in a category")
    @ApiResponse(responseCode = "200", description = "Apps retrieved successfully")
    public ResponseEntity<List<String>> getAppsInCategory(@PathVariable Long categoryId) {
        log.info("Getting apps in category: {}", categoryId);
        List<String> apps = screenTimeService.getAppsInCategory(categoryId);
        return ResponseEntity.ok(apps);
    }

    @Data
    public static class CategoryRequest {
        @NotBlank(message = "Category name is required")
        private String name;
        private String description;
    }

    @Data
    public static class AppAssignmentRequest {
        @NotBlank(message = "App name is required")
        private String appName;
    }
}