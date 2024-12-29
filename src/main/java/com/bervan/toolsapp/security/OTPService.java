package com.bervan.toolsapp.security;

import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    public static final int CODE_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, UUID> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();
    private static final long OTP_VALIDITY_DURATION = 300_000;
    private final UserRepository userRepository;

    public OTPService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateOTP(UUID userId) {
        String otp = String.format("%0" + CODE_LENGTH + "d", random.nextInt(1_000_000));
        otpStorage.put(otp, userId);
        otpExpiry.put(otp, System.currentTimeMillis() + OTP_VALIDITY_DURATION);
        return otp;
    }

    public User login(String otp) {
        UUID userId = otpStorage.get(otp);
        if (userId != null) {
            if (validateOTP(otp)) {
                return userRepository.findById(userId).get();
            }
        }

        throw new RuntimeException("Incorrect code!");
    }

    private boolean validateOTP(String otp) {
        Long expiryTime = otpExpiry.get(otp);
        if (expiryTime != null && expiryTime > System.currentTimeMillis()) {
            otpStorage.remove(otp);
            otpExpiry.remove(otp);
            return true;
        }
        return false;
    }

//    private void login(UUID userId) {
//        User user = userRepository.findById(userId).get();
//        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
////        SecurityContextHolder.getContext().setAuthentication(authentication);
//        SecurityContextHolder.createEmptyContext().setAuthentication(authentication);
//    }
}