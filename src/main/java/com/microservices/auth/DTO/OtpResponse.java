package com.microservices.auth.DTO;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class OtpResponse {
    private final String phoneNumber;
    private final String otp;
    private final LocalDateTime otpExpiryTime;

    public OtpResponse(String phoneNumber, String otp, LocalDateTime otpExpiryTime) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.otpExpiryTime = otpExpiryTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDateTime getOtpExpiryTime() {
        return otpExpiryTime;
    }
}

