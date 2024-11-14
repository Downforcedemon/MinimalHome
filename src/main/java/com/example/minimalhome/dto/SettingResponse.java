package com.example.minimalhome.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SettingResponse {

    private Long id;
    private Long userId;
    private String key;
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
