package com.haru.SwipeStyle.Components;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GoogleOAuthEnvLogger {

    private static final Logger logger = LoggerFactory.getLogger(GoogleOAuthEnvLogger.class);

    @Value("${GOOGLE_CLIENT_ID:NOT_SET}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET:NOT_SET}")
    private String googleClientSecret;

    @PostConstruct
    public void logEnvVars() {
        logger.info("Google Client ID: {}", googleClientId);
        logger.info("Google Client Secret: {}", googleClientSecret.equals("NOT_SET") ? "NOT_SET" : "********");
    }
}
