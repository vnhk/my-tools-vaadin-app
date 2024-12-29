package com.bervan.toolsapp.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class OtpAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String otp;

    public OtpAuthenticationToken(Object principal, Object credentials, String otp) {
        super(principal, credentials);
        this.otp = otp;
    }

    public OtpAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String otp) {
        super(principal, credentials, authorities);
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }
}