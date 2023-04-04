package com.microservices.auth.DTO;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
    public class VerifyOtpResponse {
        private final boolean success;
        private final String message;
        private final String jwtToken;

}
