package com.example.minimalhome.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class UserSettingsResponse {
    private Long userId;
    private int totalSettings;
    private Map<String, List<SettingResponse>> settings;

    // Optional: Helper method to organize settings by category
    public static Map<String, List<SettingResponse>> organizeByCategory(List<SettingResponse> settings) {
        // Example: "notification.email" and "notification.sms" will be grouped under "notification"
        return settings.stream()
                .collect(Collectors.groupingBy(
                        setting -> setting.getKey().split("\\.")[0],
                        Collectors.toList()
                ));
    }
}

/*
Map structure example of orgianizeByCategory:
{
    "notification": [
        {key: "notification.email", value: "true"},
        {key: "notification.sms", value: "false"}
    ],
    "theme": [
        {key: "theme.darkMode", value: "true"}
    ]
}
 */