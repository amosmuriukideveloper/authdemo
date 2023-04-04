package com.microservices.auth.Repo;

import com.microservices.auth.models.Otp;
import com.microservices.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findOtpByPhoneNumber(String phoneNumber);
    Optional<Otp> findByUser(User user);

//
}
