package com.bervan.toolsapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProfileUtils {

    private static Environment staticEnvironment;
    @Autowired
    private Environment environment;

    public static boolean isLocal() {
        return staticEnvironment != null && staticEnvironment.acceptsProfiles("local");
    }

    public static boolean isProd() {
        return staticEnvironment != null && staticEnvironment.acceptsProfiles("production") || staticEnvironment.acceptsProfiles("prod");
    }

    @PostConstruct
    public void init() {
        staticEnvironment = environment;
    }
}
