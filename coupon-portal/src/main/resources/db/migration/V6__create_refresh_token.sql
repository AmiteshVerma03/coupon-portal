CREATE TABLE IF NOT EXISTS refresh_token (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL UNIQUE,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expiry_date DATETIME     NOT NULL,

    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users (id)
);
