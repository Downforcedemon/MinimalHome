-- Screen Time Categories Table
-- Stores predefined categories for grouping apps
CREATE TABLE screen_time_categories (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(50) NOT NULL,
                                        description TEXT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Screen Time App Categories Table
-- Maps apps to their categories (many-to-many relationship)
CREATE TABLE screen_time_app_categories (
                                            id BIGSERIAL PRIMARY KEY,
                                            category_id BIGINT REFERENCES screen_time_categories(id),
                                            app_name VARCHAR(100) NOT NULL,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            UNIQUE(category_id, app_name)
);

-- Screen Time Logs Table
-- Records individual app usage sessions
CREATE TABLE screen_time_logs (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT REFERENCES users(id),
                                  app_name VARCHAR(100) NOT NULL,
                                  start_time TIMESTAMP NOT NULL,
                                  end_time TIMESTAMP,
                                  duration BIGINT,
                                  is_active BOOLEAN DEFAULT true,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Screen Time Limits Table
-- Stores user-defined time limits for categories
CREATE TABLE screen_time_limits (
                                    id BIGSERIAL PRIMARY KEY,
                                    user_id BIGINT REFERENCES users(id),
                                    category_id BIGINT REFERENCES screen_time_categories(id),
                                    daily_limit BIGINT NOT NULL, -- in seconds
                                    weekly_limit BIGINT NOT NULL, -- in seconds
                                    is_enabled BOOLEAN DEFAULT true,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    UNIQUE(user_id, category_id)
);

-- Screen Time Notifications Table
-- Tracks notifications sent to users about their screen time
CREATE TABLE screen_time_notifications (
                                           id BIGSERIAL PRIMARY KEY,
                                           user_id BIGINT REFERENCES users(id),
                                           category_id BIGINT REFERENCES screen_time_categories(id),
                                           notification_type VARCHAR(50) NOT NULL, -- 'LIMIT_WARNING', 'DAILY_SUMMARY', etc.
                                           message TEXT NOT NULL,
                                           is_read BOOLEAN DEFAULT false,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for better query performance
CREATE INDEX idx_screen_time_logs_user_id ON screen_time_logs(user_id);
CREATE INDEX idx_screen_time_logs_start_time ON screen_time_logs(start_time);
CREATE INDEX idx_screen_time_limits_user_id ON screen_time_limits(user_id);
CREATE INDEX idx_screen_time_notifications_user_id ON screen_time_notifications(user_id);

-- Insert default categories
INSERT INTO screen_time_categories (name, description) VALUES
                                                           ('Social', 'Social media and communication apps'),
                                                           ('Productivity', 'Work and productivity related apps'),
                                                           ('Entertainment', 'Entertainment and media apps'),
                                                           ('Gaming', 'Games and gaming related apps'),
                                                           ('Education', 'Learning and educational apps');


/*
erDiagram
    users ||--o{ screen_time_logs : "tracks usage"
    users ||--o{ screen_time_limits : "sets limits"
    users ||--o{ screen_time_notifications : "receives"
    
    screen_time_categories ||--|{ screen_time_app_categories : "contains"
    screen_time_categories ||--|{ screen_time_limits : "has limits"
    screen_time_categories ||--|{ screen_time_notifications : "triggers"

    users {
        bigserial id PK
        varchar username
        varchar email
        varchar password_hash
        timestamp created_at
        timestamp updated_at
    }

    screen_time_categories {
        bigserial id PK
        varchar name
        text description
        timestamp created_at
        timestamp updated_at
    }

    screen_time_app_categories {
        bigserial id PK
        bigint category_id FK
        varchar app_name
        timestamp created_at
    }

    screen_time_logs {
        bigserial id PK
        bigint user_id FK
        varchar app_name
        timestamp start_time
        timestamp end_time
        bigint duration
        boolean is_active
        timestamp created_at
    }

    screen_time_limits {
        bigserial id PK
        bigint user_id FK
        bigint category_id FK
        bigint daily_limit
        bigint weekly_limit
        boolean is_enabled
        timestamp created_at
        timestamp updated_at
    }

    screen_time_notifications {
        bigserial id PK
        bigint user_id FK
        bigint category_id FK
        varchar notification_type
        text message
        boolean is_read
        timestamp created_at
    }
*/ 
