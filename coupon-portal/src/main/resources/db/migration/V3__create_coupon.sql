CREATE TABLE IF NOT EXISTS coupon (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(100) NOT NULL UNIQUE,
    platform    VARCHAR(100) NOT NULL,
    course      VARCHAR(200) NOT NULL,
    expiry_date DATE         NOT NULL,
    usage_limit INT          NOT NULL DEFAULT 1,
    used_count  INT          NOT NULL DEFAULT 0,
    tenant_id   BIGINT       NOT NULL,

    CONSTRAINT fk_coupon_tenant FOREIGN KEY (tenant_id) REFERENCES tenant (id)
);

CREATE INDEX idx_coupon_tenant_id ON coupon (tenant_id);
CREATE INDEX idx_coupon_platform  ON coupon (platform);
CREATE INDEX idx_coupon_course    ON coupon (course);
