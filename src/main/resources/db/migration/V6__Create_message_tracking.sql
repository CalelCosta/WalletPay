-- V6__Create_message_tracking.sql
CREATE TABLE processed_messages (
                                    message_id VARCHAR(255) PRIMARY KEY,
                                    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);