package com.bervan.toolsapp.security;

import com.bervan.common.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class OtpAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private final OTPService otpService;
    private final UserDetailsService userDetailsService;

    public OtpAuthenticationProvider(OTPService otpService, UserDetailsService userDetailsService) {
        super();
        this.userDetailsService = userDetailsService;
        setUserDetailsService(userDetailsService);
        this.otpService = otpService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OtpAuthenticationToken authRequest = (OtpAuthenticationToken) authentication;
        String otp = authRequest.getOtp();

        User user = otpService.login(otp);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );

        Authentication authenticate = super.authenticate(usernamePasswordAuthenticationToken);

        return authenticate;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }
}