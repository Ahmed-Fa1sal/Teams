package com.teams.teams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Separate configuration class for PasswordEncoder bean.
 * This class is responsible for creating and providing the PasswordEncoder bean,
 * avoiding circular dependencies with SecurityConfig.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Creates a BCryptPasswordEncoder bean for password encoding/hashing.
     * BCrypt is a widely used, secure algorithm for password hashing.
     *
     * @return PasswordEncoder bean configured with BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

