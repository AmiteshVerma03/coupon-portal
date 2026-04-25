package com.couponportal.exception;

public class CouponUnavailableException extends RuntimeException {
    public CouponUnavailableException(String message) {
        super(message);
    }
}
