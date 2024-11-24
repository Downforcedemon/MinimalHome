package com.example.minimalhome.dto;

import com.example.minimalhome.entity.ScreenTimeCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryAssignmentResponse {
    private String message;
    private String appName;
    private Long categoryId;
    private String categoryName;
    private String description;
    private boolean success;

    public static CategoryAssignmentResponse fromCategory(ScreenTimeCategory category) {
        return CategoryAssignmentResponse.builder()
                .success(true)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .description(category.getDescription())
                .build();
    }

    public static CategoryAssignmentResponse fromAssignment(String appName, ScreenTimeCategory category) {
        return CategoryAssignmentResponse.builder()
                .success(true)
                .appName(appName)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .message(String.format("App '%s' has been successfully assigned to '%s' category",
                        appName, category.getName()))
                .build();
    }

    public static CategoryAssignmentResponse error(String message) {
        return CategoryAssignmentResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}