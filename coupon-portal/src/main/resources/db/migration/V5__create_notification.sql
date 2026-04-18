CREATE TABLE IF NOT EXISTS notification (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    message     VARCHAR(500) NOT NULL,
    type        ENUM(
                    'COUPON_ASSIGNED',
                    'REQUEST_APPROVED',
                    'REQUEST_REJECTED',
                    'RECOMMENDATION',
                    'GENERAL'
                )            NOT NULL,
    read_status BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_notification_user_id     ON notification (user_id);
CREATE INDEX idx_notification_read_status ON notification (read_status);
