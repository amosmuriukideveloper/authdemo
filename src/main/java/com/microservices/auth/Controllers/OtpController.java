package com.microservices.auth.Controllers;

import com.microservices.auth.DTO.SendOtpDTO;
import com.microservices.auth.DTO.SmsRequest;
import com.microservices.auth.DTO.UniversalResponse;
import com.microservices.auth.DTO.VerifyOtpDTO;
import com.microservices.auth.Repo.OtpRepository;
import com.microservices.auth.Services.TwilioSmsSender;

import com.microservices.auth.models.Otp;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
@Transactional
public class OtpController {

    private final TwilioSmsSender twilioSmsSender;
    private final OtpRepository otpRepository;

    public OtpController(TwilioSmsSender twilioSmsSender, OtpRepository otpRepository) {
        this.twilioSmsSender = twilioSmsSender;
        this.otpRepository = otpRepository;
    }

    @PostMapping("/otp/send")
    public UniversalResponse sendOtp(@RequestBody SendOtpDTO request) {
        try {
            Random random = new Random();
            int otp = random.nextInt(9000) + 1000;

            SmsRequest smsRequest = SmsRequest.builder()
                    .phoneNumber(request.getPhoneNumber()).message(otp)
                    .build();

            try {
                twilioSmsSender.SendSms(smsRequest);
            } catch (Exception ex) {
                System.out.println("Failed to send OTP: " + ex.getMessage());
                // return ex.getMessage();
            }

            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

            Otp otpObj = Otp.builder()
                    .phoneNumber(request.getPhoneNumber())
                    .otp(otp)
                    .otpExpiryTime(expiryTime)
                    .build();

            otpRepository.save(otpObj);




            return new UniversalResponse(HttpStatus.OK.value(), "OTP sent successfully", null);
        } catch (Exception ex) {
            System.out.println("Error sending OTP: " + ex.getMessage());
            return new UniversalResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error sending OTP", null);
        }
    }

    @PostMapping("/otp/verify")
    public UniversalResponse verifyOtp(@RequestBody VerifyOtpDTO request) {
        Otp otp = otpRepository.findOtpByPhoneNumber(request.getPhoneNumber());

        if (otp == null) {
            return new UniversalResponse(HttpStatus.BAD_REQUEST.value(), "Mobile number not found", null);
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = otp.getOtpExpiryTime();
        Duration duration = Duration.between(currentTime, expiryTime);
        long minutes = duration.toMinutes();

        if (minutes >= 5) {
            otpRepository.delete(otp);
            return new UniversalResponse(HttpStatus.BAD_REQUEST.value(), "OTP expired", null);
        }

        if (otp.getOtp() == request.getOtp()) {
            otpRepository.delete(otp);
            return new UniversalResponse(HttpStatus.OK.value(), "OTP verified", null);
        }

        return new UniversalResponse(HttpStatus.BAD_REQUEST.value(), "Invalid OTP", null);
    }
}
