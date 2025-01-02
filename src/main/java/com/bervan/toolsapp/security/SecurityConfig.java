package com.bervan.toolsapp.security;

import com.bervan.toolsapp.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final AuthenticationProvider otpAuthenticationProvider;

    public SecurityConfig(
            CustomAuthenticationProvider customAuthenticationProvider
    ) {
        this.otpAuthenticationProvider = customAuthenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            CustomAuthenticationProvider otpProvider
    ) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(otpProvider);
        return authBuilder.build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure()
                ).authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/login", "/pocket/**", "/language-learning/**").permitAll();
                    authorizationManagerRequestMatcherRegistry.requestMatchers("/line-awesome/**", "/static/**", "/images/**").permitAll();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer.loginPage("/login").permitAll();
                    httpSecurityFormLoginConfigurer.defaultSuccessUrl("/");
                    httpSecurityFormLoginConfigurer.successForwardUrl("/");
                })
                .authenticationProvider(otpAuthenticationProvider)
                .logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer.logoutUrl("/logout");
                    httpSecurityLogoutConfigurer.logoutSuccessUrl("/login");
                });


        setLoginView(http, LoginView.class);
        super.configure(http);

        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                .csrf().disable();

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );

    }
}