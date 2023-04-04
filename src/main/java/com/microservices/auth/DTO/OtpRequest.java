package com.microservices.auth.DTO;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequest {



        private String email;

        private String otp;

        private String otpExpiryTime;

        @Column(name = "phone_number", unique = true)
        private String phoneNumber;






}
