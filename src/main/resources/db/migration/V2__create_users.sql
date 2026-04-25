CREATE TABLE IF NOT EXISTS users (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(100)                        NOT NULL,
    email     VARCHAR(150)                        NOT NULL UNIQUE,
    password  VARCHAR(255)                        NOT NULL,
    role      ENUM('ADMIN', 'MANAGER', 'USER')    NOT NULL,
    tenant_id BIGINT                              NOT NULL,

    CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id)
);

CREATE INDEX idx_user_email     ON users (email);
CREATE INDEX idx_user_tenant_id ON users (tenant_id);
