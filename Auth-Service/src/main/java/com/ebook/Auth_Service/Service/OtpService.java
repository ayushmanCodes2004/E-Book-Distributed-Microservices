package com.ebook.Auth_Service.Service;

import com.ebook.Auth_Service.Config.RedisConfig;
import com.ebook.Auth_Service.Entity.UserCredential;
import com.ebook.Auth_Service.Exception.InvalidOtpException;
import com.ebook.Auth_Service.Exception.OtpExpiredException;
import com.ebook.Auth_Service.Exception.UserNotFoundException;
import com.ebook.Auth_Service.Repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final UserCredentialRepository userCredentialRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    @Value("${otp.expiry.minutes}")
    private long expiryTime;

    private String generateOtp() {

        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);

    }

    public String createAndStoreOtp(Integer phoneNumber) {
        Optional<UserCredential> user = userCredentialRepository.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with phone number: " + phoneNumber);
        }
        String otp = generateOtp();

        String key = "OTP:" + phoneNumber;
        String hashedOtp = passwordEncoder.encode(otp);
        redisTemplate.opsForValue().set(key, hashedOtp);
        redisTemplate.expire(key, java.time.Duration.ofMinutes(expiryTime));
        return otp;
    }

    public boolean validateOtp(Integer phoneNumber, String otp) {
        String key = "OTP:" + phoneNumber;
        String storedHashedOtp = redisTemplate.opsForValue().get(key);

        if (storedHashedOtp == null) {
            throw new OtpExpiredException("OTP has expired or does not exist for phone number: " + phoneNumber);
        }

        if (!passwordEncoder.matches(otp, storedHashedOtp)) {
            throw new InvalidOtpException("Invalid OTP provided for phone number: " + phoneNumber);
        }

        redisTemplate.delete(key);

        return true;
    }

}
