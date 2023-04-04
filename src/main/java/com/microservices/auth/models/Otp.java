package com.microservices.auth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime otpExpiryTime;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private int otp;
    private String phoneNumber;

    public Otp(User user, String otp) {
        this.user = user;
        this.otp = Integer.parseInt(otp);
        this.otpExpiryTime = LocalDateTime.now().plusMinutes(5);
    }




    public Otp(String phoneNumber, String otp) {
        this.phoneNumber = phoneNumber;
        this.otp = Integer.parseInt(otp);
        this.otpExpiryTime = LocalDateTime.now().plusMinutes(5);
    }
}

