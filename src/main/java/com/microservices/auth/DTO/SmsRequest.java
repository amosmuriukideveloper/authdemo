package com.microservices.auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SmsRequest {
    private String phoneNumber;//destination phone number
    private int message;
    private String otp;




    public SmsRequest(String phoneNumber, String message, String otp) {
        this.phoneNumber = phoneNumber;
        this.message = Integer.parseInt(message);
        this.otp = otp;
    }


    @Override
    public String toString() {
        return "SmsRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", message=" + message +
                '}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getMessage() {
        return message;
    }
}