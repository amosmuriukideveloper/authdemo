// Import Statements
package com.microservices.auth.Services;

import com.microservices.auth.Config.JwtService;
import com.microservices.auth.DTO.*;
import com.microservices.auth.Exceptions.UserNotFoundException;
import com.microservices.auth.Repo.OtpRepository;
import com.microservices.auth.Repo.UserRepository;
import com.microservices.auth.models.Otp;
import com.microservices.auth.models.Role;
import com.microservices.auth.models.User;
import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpRepository otpRepository;

    UniversalResponse response;


    private final TwilioSmsSender twilioSmsSender;
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    // Constructor to initialize dependencies
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                                 AuthenticationManager authenticationManager, OtpRepository otpRepository, TwilioSmsSender twilioSmsSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.otpRepository = otpRepository;

        this.twilioSmsSender = twilioSmsSender;
    }


    // Method to register a new user
    public RegisterResponse register(RegisterRequest request) {
        // Convert email to lowercase
        String email = request.getEmail().toLowerCase();
        String phoneNumber = request.getPhoneNumber();

        // Check if user already exists with given email or phone number
        Optional<User> userByEmail = userRepository.findByEmail(email);
        Optional<User> userByPhoneNumber = userRepository.findByPhoneNumber(phoneNumber);

        if (userByEmail.isPresent()) {
            return RegisterResponse.builder().response("Email already exists").build();
        }

        if (userByPhoneNumber.isPresent()) {
            return RegisterResponse.builder().response("Mobile number already exists").build();
        }

        // Create a new user
        User newUser = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(email)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(request.getPassword()))
                .repeatPassword(passwordEncoder.encode(request.getRepeatPassword()))
                .role(Role.USER)
                .build();

        // Save the user to database
        userRepository.save(newUser);


        // Generate OTP
        int otp = (int) (Math.random() * 10000);

// Create OTP object
        Otp newOtp = Otp.builder()
                .phoneNumber(phoneNumber)
                .otp(otp)
                .otpExpiryTime(LocalDateTime.now().plusMinutes(5)) // OTP expires in 5 minutes
                .build();

// Save OTP to database
        otpRepository.save(newOtp);

// Send SMS with OTP
//        SmsRequest smsRequest = new SmsRequest();
//        smsRequest.setPhoneNumber(phoneNumber);
//        smsRequest.setMessage(otp);
//        twilioSmsSender.SendSms(smsRequest);


        // Log the registration
        LOGGER.info("User registered successfully with email: {}", email);

        // Return success response
        return RegisterResponse.builder().response("Registration successful").build();
    }


    // Method to verify OTP for user registration
    public UniversalResponse verifyOtp(OtpVerificationRequest request) {
        // Find the OTP object for the phone number
        Optional<Otp> otpOptional = Optional.ofNullable(otpRepository.findOtpByPhoneNumber(request.getPhoneNumber()));

        // If OTP object is not found, return error response
        if (otpOptional.isEmpty()) {
            return UniversalResponse.builder().message("OTP not found").build();
        }

        // Get the OTP object
        Otp otp = otpOptional.get();

        // Verify the OTP
        int otpValue = Integer.parseInt(request.getOtp());
        UniversalResponse response;

        if (otp.getOtp() == otpValue && otp.getOtpExpiryTime().isAfter(LocalDateTime.now())) {
            // Delete the OTP from database
            otpRepository.delete(otp);

            // Set success response
            response = UniversalResponse.builder().message("OTP verification successful").build();
        } else {
            // Set error response
            response = UniversalResponse.builder().message("Invalid OTP").build();
        }

// Return the response
        return response;
    }


    // Method to authenticate a user


    public UniversalResponse login(AuthenticationRequest request) throws InvalidCredentialsException, UserNotFoundException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token for the user
            UserDetails userDetails = loadUserByUsername(request.getEmail());
            String jwt = jwtService.createToken(request.getEmail());

            // Return success response with JWT token
            UniversalResponse response = UniversalResponse.builder()
                    .message(jwt)
                    .build();
            return response;
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            LOGGER.error("Invalid Credentials for user with email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase());

        // If user is not found, throw exception
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Return UserDetails object for the user
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole().toString()));
    }
}