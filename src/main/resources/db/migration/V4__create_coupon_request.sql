CREATE TABLE IF NOT EXISTS coupon_request (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT                                  NOT NULL,
    course             VARCHAR(200)                            NOT NULL,
    platform           VARCHAR(100)                            NOT NULL,
    status             ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    assigned_coupon_id BIGINT                                  NULL,
    created_at         DATETIME                                NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME                                NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_user           FOREIGN KEY (user_id)            REFERENCES users  (id),
    CONSTRAINT fk_request_assigned_coupon FOREIGN KEY (assigned_coupon_id) REFERENCES coupon (id)
);

CREATE INDEX idx_request_user_id   ON coupon_request (user_id);
CREATE INDEX idx_request_status    ON coupon_request (status);
