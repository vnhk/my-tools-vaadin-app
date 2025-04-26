package com.bervan.toolsapp;

import com.bervan.common.BervanBaseRepositoryImpl;
import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.core.model.BervanLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.bervan.*")
@EnableJpaRepositories(basePackages = "com.bervan.*", repositoryBaseClass = BervanBaseRepositoryImpl.class)
@EntityScan(basePackages = "com.bervan.*")
@EnableMethodSecurity
public class Application extends SpringBootServletInitializer {
    private Logger logger = LoggerFactory.getLogger(Application.class);
    private final UserRepository userRepository;

    public Application(UserRepository userRepository) {
        this.userRepository = userRepository;

        if (userRepository.findByUsername("COMMON_USER").isEmpty()) {
            User save = new User();
            save.setUsername("COMMON_USER");
            save.setRole("ROLE_USER");
            save.setLockedAccount(true);
            userRepository.save(save);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public BervanLogger bervanLogger() {

        return new BervanLogger() {
            @Override
            public void error(String message) {
                logger.error(message);
            }

            @Override
            public void info(String message) {
                logger.info(message);
            }

            @Override
            public void debug(String message) {
                logger.debug(message);
            }

            @Override
            public void warn(String message) {
                logger.warn(message);
            }

            @Override
            public void error(String message, Throwable throwable) {
                logger.error(message, throwable);
            }

            @Override
            public void warn(String message, Throwable throwable) {
                logger.warn(message, throwable);
            }

            @Override
            public void error(Throwable throwable) {
                logger.error("Error:", throwable);
            }

            @Override
            public void warn(Throwable throwable) {
                logger.warn("Error:", throwable);
            }
        };
    }
}
