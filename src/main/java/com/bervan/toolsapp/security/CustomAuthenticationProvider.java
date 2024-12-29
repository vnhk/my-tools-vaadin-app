package com.bervan.toolsapp.security;

import com.bervan.common.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private final OTPService otpService;

    public CustomAuthenticationProvider(OTPService otpService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        super();
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
        this.otpService = otpService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof OtpAuthenticationToken authRequest) {
            String otp = authRequest.getOtp();
            User user = otpService.login(otp);

            OtpAuthenticationToken usernamePasswordAuthenticationToken = new OtpAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities(),
                    otp
            );

            Authentication authenticate = super.authenticate(usernamePasswordAuthenticationToken);

            return authenticate;
        } else {
            return super.authenticate(authentication);
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (!(authentication instanceof OtpAuthenticationToken)) {
            super.additionalAuthenticationChecks(userDetails, authentication);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}