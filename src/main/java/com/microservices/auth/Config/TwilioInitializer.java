package com.microservices.auth.Config;

import com.twilio.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioInitializer {
    private final static Logger LOGGER= LoggerFactory.getLogger(TwilioInitializer.class);
    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioInitializer(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getTwilioAccountSid(),twilioConfig.getTwilioAuthToken());
        LOGGER.info("twilio initialized....");
    }
}
