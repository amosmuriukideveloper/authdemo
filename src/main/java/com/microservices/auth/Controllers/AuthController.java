package com.microservices.auth.Controllers;

import com.microservices.auth.DTO.*;
import com.microservices.auth.Repo.OtpRepository;
import com.microservices.auth.Services.AuthenticationService;
import com.microservices.auth.models.Otp;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;

    private final OtpRepository otpRepository;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request)
    {


        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<UniversalResponse> verifyOtp(@RequestBody VerifyOtpDTO request) {
        Otp otp = otpRepository.findOtpByPhoneNumber(request.getPhoneNumber());

        if (otp == null) {
            return ResponseEntity.badRequest()
                    .body(new UniversalResponse(HttpStatus.BAD_REQUEST.value(), "Mobile number not found", null));
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = otp.getOtpExpiryTime();
        Duration duration = Duration.between(currentTime, expiryTime);
        long minutes = duration.toMinutes();

        if (minutes >= 5) {
            otpRepository.delete(otp);
            return ResponseEntity.badRequest()
                    .body(new UniversalResponse(HttpStatus.BAD_REQUEST.value(), "OTP expired", null));
        }

        if (otp.getOtp() == request.getOtp()) {
            otpRepository.delete(otp);
            return ResponseEntity.ok()
                    .body(new UniversalResponse(HttpStatus.OK.value(), "OTP verified", null));
        }

        return ResponseEntity.badRequest()
                .body(new UniversalResponse(HttpStatus.BAD_REQUEST.value(), "Invalid OTP", null));
    }



    @PostMapping("/authenticate")
    public ResponseEntity<UniversalResponse> login(
            @RequestBody AuthenticationRequest request
    ) throws InvalidCredentialsException {
        return ResponseEntity.ok(service.login(request));

    }
}
