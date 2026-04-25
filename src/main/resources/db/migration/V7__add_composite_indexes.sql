-- Task 4: composite indexes added in V7
-- These cover the two most common multi-column filter queries in the application

-- coupon_request: manager dashboard filters by tenant (via user) + status together
-- findAllByTenantIdAndStatus uses both columns in every call
ALTER TABLE coupon_request
    ADD INDEX idx_request_user_status (user_id, status);

-- notification: unread-count and unread-list both filter by (user_id, read_status)
-- countByUserIdAndReadStatusFalse and findAllByUserIdAndReadStatusFalse use both columns
ALTER TABLE notification
    ADD INDEX idx_notification_user_read (user_id, read_status);
