//package com.microservices.auth.Services;
//
//import com.microservices.auth.DTO.OtpResponse;
//import com.microservices.auth.Repo.UserRepository;
//import com.microservices.auth.models.User;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//
//public class UserService implements UserDetailsService {
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JavaMailSender javaMailSender;
//    private final TwilioSmsSender twilioService;
//    private OtpResponse otp;
//
//    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, TwilioSmsSender twilioService) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.javaMailSender = javaMailSender;
//        this.twilioService = twilioService;
//
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String emailOrPhoneNumber) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(emailOrPhoneNumber)
//                .orElse(userRepository.findByPhoneNumber(emailOrPhoneNumber)
//                        .orElseThrow(() -> new UsernameNotFoundException("Invalid email or mobile number")));
//
//        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
//    }
//
//    public User register(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        return userRepository.save(user);
//    }
//
//    public void sendVerificationEmail(String email) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Verify Your Email Address");
//        message.setText("Please click on the following link to verify your email address: http://localhost:8080/verify?email=" + email);
//        javaMailSender.send(message);
//    }
//
////    public void sendVerificationSMS(String phoneNumber) {
////        String verificationCode = generateVerificationCode();
////        twilioService.sendSms( "Your verification code is: " + verificationCode, Integer.parseInt(otp.getOtp()));
////    }
//
////    private String generateVerificationCode() {
////        // implementation for generating a verification code
////        return "1234"; // temporary implementation for testing purposes
////    }
//
////    public void verifyEmail(String email, String verificationCode) {
////        User user = userRepository.findByEmail(email)
////                .orElseThrow(() -> new UsernameNotFoundException("Invalid email"));
////        if (user.isEmailVerified()) {
////            throw new RuntimeException("Email is already verified");
////        }
////        if (verificationCode.equals(user.getEmailVerificationCode())) {
////            user.setEmailVerified(true);
////            userRepository.save(user);
////        } else {
////            throw new RuntimeException("Invalid verification code");
////        }
////    }
//
////    public void verifySMS(String phoneNumber, String verificationCode) {
////        User user = userRepository.findByPhoneNumber(phoneNumber)
////                .orElseThrow(() -> new UsernameNotFoundException("Invalid phone number"));
////        if (user.isMobileNumberVerified()) {
////            throw new RuntimeException("Mobile number is already verified");
////        }
////        if (verificationCode.equals(user.getMobileNumberVerificationCode())) {
////            user.setMobileNumberVerified(true);
////            userRepository.save(user);
////        } else {
////            throw new RuntimeException("Invalid verification code");
////        }
////    }
//
//
//}
