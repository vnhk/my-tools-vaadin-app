package com.bervan.toolsapp;

import com.bervan.history.model.BaseRepositoryImpl;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@Theme(value = "my-theme")
@SpringBootApplication
@NpmPackage(value = "line-awesome", version = "1.3.0")
@ComponentScan(basePackages = "com.bervan.*")
@EnableJpaRepositories(basePackages = "com.bervan.*", repositoryBaseClass = BaseRepositoryImpl.class)
@EntityScan(basePackages = "com.bervan.*")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
