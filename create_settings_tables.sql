-- create app_settings
CREATE TABLE app_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    setting_key VARCHAR(255),
    setting_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

--create index
-- For quick lookups of user settings
CREATE INDEX idx_app_settings_user_id ON app_settings(user_id);

-- For enforcing unique keys per user
CREATE UNIQUE INDEX idx_app_settings_user_key ON app_settings(user_id, setting_key);

-- Screen Time Logs Table
CREATE TABLE screen_time_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    app_name VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    duration BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_duration CHECK (duration >= 0),
    CONSTRAINT chk_time_order CHECK (end_time >= start_time)
);

-- App Usage Stats Table
CREATE TABLE app_usage_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    app_name VARCHAR(255) NOT NULL,
    total_duration BIGINT NOT NULL DEFAULT 0,
    launch_count INT NOT NULL DEFAULT 0,
    usage_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_daily_app_usage UNIQUE (user_id, app_name, usage_date),
    CONSTRAINT chk_total_duration CHECK (total_duration >= 0),
    CONSTRAINT chk_launch_count CHECK (launch_count >= 0)
);

-- Notification Settings Table
CREATE TABLE notification_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    app_name VARCHAR(255) NOT NULL,
    is_filtered BOOLEAN NOT NULL DEFAULT false,
    priority_level INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT unique_user_app_notification UNIQUE (user_id, app_name),
    CONSTRAINT chk_priority_level CHECK (priority_level BETWEEN 0 AND 5)
);

-- Notification Logs Table
CREATE TABLE notification_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    app_name VARCHAR(255) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    is_blocked BOOLEAN NOT NULL DEFAULT false,
    priority_level INT NOT NULL,
    received_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_priority_level CHECK (priority_level BETWEEN 0 AND 5)
);


-- Focus Mode Settings Table
CREATE TABLE focus_mode_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_enabled BOOLEAN NOT NULL DEFAULT false,
    start_time TIME,
    end_time TIME,
    days_of_week VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT unique_user_focus_mode UNIQUE (user_id),
    CONSTRAINT chk_days_of_week CHECK (days_of_week ~ '^[0-6](,[0-6])*$')
);

-- Create indexes for better query performance
CREATE INDEX idx_screen_time_logs_user_id_app_name ON screen_time_logs(user_id, app_name);
CREATE INDEX idx_screen_time_logs_start_time ON screen_time_logs(start_time);
CREATE INDEX idx_app_usage_stats_user_id_date ON app_usage_stats(user_id, usage_date);
CREATE INDEX idx_notification_logs_user_id_app ON notification_logs(user_id, app_name);
CREATE INDEX idx_notification_logs_received_at ON notification_logs(received_at);
CREATE INDEX idx_app_settings_user_id ON app_settings(user_id);

--- to retrieve description
SELECT obj_description(oid) AS comment, relname AS table_name
FROM pg_class
WHERE relkind = 'r' AND relname IN ('users', 'app_settings', 'screen_time_logs', 'app_usage_stats', 'notification_settings', 'notification_logs', 'focus_mode_settings');
