package com.microservices.auth.Services;

import com.microservices.auth.Config.TwilioConfig;
import com.microservices.auth.DTO.SmsRequest;
import com.microservices.auth.Repo.OtpRepository;
import com.microservices.auth.models.Otp;
import com.microservices.auth.models.User;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TwilioSmsSender {
    private final OtpRepository otpRepository;
    private final TwilioConfig twilioConfig;
    private final static Logger LOGGER = LoggerFactory.getLogger(TwilioSmsSender.class);

    @Autowired
    public TwilioSmsSender(OtpRepository otpRepository, TwilioConfig twilioConfig) {
        super();
        this.otpRepository = otpRepository;
        this.twilioConfig = twilioConfig;
    }

    public void SendSms(SmsRequest smsRequest) {
        try {
            LocalDateTime Expiry = LocalDateTime.now().plusMinutes(5);
            Message twilioMessage = Message.creator(
                    new PhoneNumber(smsRequest.getPhoneNumber()),
                    new PhoneNumber(twilioConfig.getTrial_number()),
                    String.valueOf(smsRequest.getMessage())
            ).create();
            Otp otpObj = new Otp();
            Otp otp1=otpObj.builder()
                    .otpExpiryTime(Expiry).phoneNumber(smsRequest.getPhoneNumber()).otp(smsRequest.getMessage()).build();
            otpRepository.save(otp1);
        } catch (Exception ex) {
            System.out.println("Error While Sending OTP"+ex);
        }
        LOGGER.info("Send sms {}" + smsRequest);;
    }


}

