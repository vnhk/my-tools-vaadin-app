package com.bervan.toolsapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProfileUtils {
    
    @Autowired
    private Environment environment;
    
    private static Environment staticEnvironment;
    
    @PostConstruct
    public void init() {
        staticEnvironment = environment;
    }
    
    public static boolean isLocal() {
        return staticEnvironment != null && staticEnvironment.acceptsProfiles("local");
    }
    
    public static boolean isProd() {
        return staticEnvironment != null && staticEnvironment.acceptsProfiles("prod");
    }
}
