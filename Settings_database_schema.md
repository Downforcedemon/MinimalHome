erDiagram
    users ||--o{ app_settings : has
    users ||--o{ screen_time_logs : tracks
    users ||--o{ notification_settings : configures
    users ||--o{ focus_mode_settings : manages
    users ||--o{ app_usage_stats : records
    users ||--o{ notification_logs : receives
    
    users {
        bigserial id PK
        varchar username UK
        varchar email UK
        varchar password_hash
        timestamp created_at
        timestamp updated_at
    }

    app_settings {
        bigserial id PK
        bigint user_id FK
        varchar setting_key
        text setting_value
        timestamp created_at
        timestamp updated_at
    }

    screen_time_logs {
        bigserial id PK
        bigint user_id FK
        varchar app_name
        timestamp start_time
        timestamp end_time
        bigint duration
        timestamp created_at
    }

    notification_settings {
        bigserial id PK
        bigint user_id FK
        varchar app_name
        boolean is_filtered
        int priority_level
        timestamp created_at
        timestamp updated_at
    }

    focus_mode_settings {
        bigserial id PK
        bigint user_id FK
        boolean is_enabled
        time start_time
        time end_time
        varchar days_of_week
        timestamp created_at
        timestamp updated_at
    }

    app_usage_stats {
        bigserial id PK
        bigint user_id FK
        varchar app_name
        bigint total_duration
        int launch_count
        date usage_date
        timestamp created_at
    }

    notification_logs {
        bigserial id PK
        bigint user_id FK
        varchar app_name
        varchar notification_type
        text content
        boolean is_blocked
        int priority_level
        timestamp received_at
        timestamp created_at
    }
