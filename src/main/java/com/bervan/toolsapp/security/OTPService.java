package com.bervan.toolsapp.security;

import com.bervan.common.user.User;
import com.bervan.common.user.UserRepository;
import com.bervan.common.user.UserToUserRelation;
import com.bervan.logging.JsonLogger;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    public static final int CODE_LENGTH = 8;
    private static final long OTP_VALIDITY_DURATION = 300_000;
    private final JsonLogger log = JsonLogger.getLogger(getClass(), "my-tools-app");
    private final SecureRandom random = new SecureRandom();
    private final Map<String, UUID> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiry = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    public OTPService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateOTP(UUID userId, String role) {
        List<UserToUserRelation> childrenWithRole = userRepository.findById(userId).get().getChildrenRelations().stream().filter(e -> e.getChild().getRole().equals(role))
                .toList();

        if (childrenWithRole.size() != 1) {
            log.error("Incorrect configuration. Sub users with role = " + role + ": " + childrenWithRole.size());
            throw new RuntimeException("Incorrect configuration!");
        }
        userId = childrenWithRole.get(0).getChild().getId();

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
}