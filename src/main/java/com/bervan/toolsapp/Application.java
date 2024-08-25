package com.bervan.toolsapp;

import com.bervan.common.model.BervanLogger;
import com.bervan.history.model.BaseRepositoryImpl;
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
@EnableJpaRepositories(basePackages = "com.bervan.*", repositoryBaseClass = BaseRepositoryImpl.class)
@EntityScan(basePackages = "com.bervan.*")
public class Application extends SpringBootServletInitializer {
    private Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public BervanLogger bervanLogger() {
        return new BervanLogger() {
            @Override
            public void logError(String message) {
                logger.error(message);
            }

            @Override
            public void logInfo(String message) {
                logger.info(message);
            }

            @Override
            public void logDebug(String message) {
                logger.debug(message);
            }

            @Override
            public void logWarn(String message) {
                logger.warn(message);
            }

            @Override
            public void logError(String message, Throwable throwable) {
                logger.error(message, throwable);
            }

            @Override
            public void logWarn(String message, Throwable throwable) {
                logger.warn(message, throwable);
            }

            @Override
            public void logError(Throwable throwable) {
                logger.error("Error:", throwable);
            }

            @Override
            public void logWarn(Throwable throwable) {
                logger.warn("Error:", throwable);
            }
        };
    }
}
