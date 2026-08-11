package com.ebook.Auth_Service.Service;

import com.ebook.Auth_Service.DTO.UserDTO;
import com.ebook.Auth_Service.DTO.UserResponseDTO;
import com.ebook.Auth_Service.Entity.UserCredential;
import com.ebook.Auth_Service.Exception.InvalidCustomerIdException;
import com.ebook.Auth_Service.Repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialRepository userCredentialRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final EmailPublisher emailPublisher;

    private final OtpService otpService;

    public String registerUser(UserDTO user){

        // Check if username already exists
        if (userCredentialRepository.existsByUserName(user.getUserName())) {
            throw new com.ebook.Auth_Service.Exception.UserAlreadyExistsException(
                    "Username '" + user.getUserName() + "' is already taken. Please choose a different username."
            );
        }

        // Check if email already exists
        if (userCredentialRepository.existsByEmail(user.getEmail())) {
            throw new com.ebook.Auth_Service.Exception.UserAlreadyExistsException(
                    "Email '" + user.getEmail() + "' is already registered. Please use a different email or try logging in."
            );
        }

        // Check if phone number already exists
        if (userCredentialRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new com.ebook.Auth_Service.Exception.UserAlreadyExistsException(
                    "Phone number '" + user.getPhoneNumber() + "' is already registered. Please use a different phone number."
            );
        }

        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        String customerId = "CUST-" + number;

        UserCredential userCredential = new UserCredential();
        userCredential.setPassword(passwordEncoder.encode(user.getPassword()));
        userCredential.setUserName(user.getUserName());
        userCredential.setEmail(user.getEmail());
        userCredential.setCustomerId(customerId);
        userCredential.setPhoneNumber((user.getPhoneNumber()));

        userCredentialRepository.save(userCredential);

        // Send HTML welcome email (non-blocking, failure won't affect registration)
        try {
            emailPublisher.publishHtmlEmailMessage(
                    userCredential.getEmail(),
                    "Welcome to eBook Store",
                    "welcome-email",
                    Map.of(
                            "userName", userCredential.getUserName(),
                            "customerId", customerId,
                            "email", userCredential.getEmail(),
                            "phoneNumber", userCredential.getPhoneNumber().toString()
                    )
            );
        } catch (Exception e) {
            // Log the error but don't fail the registration
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return "User registered successfully with customerId: " + customerId;

    }

    public String generateToken(String userName){
        return jwtService.generateToken(userName);

    }

    public void validateToken(String token){
        jwtService.validateToken(token);
    }

    public UserResponseDTO validCustomerId(String customerId){
        Optional<UserCredential> userCredential =  userCredentialRepository.findByCustomerId(customerId);
        if(userCredential.isEmpty()){
            throw new InvalidCustomerIdException("Invalid Customer ID: " + customerId);
        }

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setCustomerId(userCredential.get().getCustomerId());
        userResponseDTO.setUserName(userCredential.get().getUserName());
        userResponseDTO.setEmail(userCredential.get().getEmail());

        return userResponseDTO;
    }

    public String requestPasswordResetOtp(Long phoneNumber) {
        Optional<UserCredential> user = userCredentialRepository.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) {
            throw new com.ebook.Auth_Service.Exception.UserNotFoundException("User not found with phone number: " + phoneNumber);
        }

        String otp = otpService.createAndStoreOtp(phoneNumber);

        // Send HTML OTP email (non-blocking)
        try {
            emailPublisher.publishHtmlEmailMessage(
                    user.get().getEmail(),
                    "Password Reset OTP - eBook Store",
                    "otp-email",
                    Map.of(
                            "otp", otp,
                            "expiryMinutes", "5"
                    )
            );
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return "OTP sent successfully to your registered email";
    }

    public String resetPassword(Long phoneNumber, String otp, String newPassword) {
        otpService.validateOtp(phoneNumber, otp);

        Optional<UserCredential> userOptional = userCredentialRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isEmpty()) {
            throw new com.ebook.Auth_Service.Exception.UserNotFoundException("User not found with phone number: " + phoneNumber);
        }

        UserCredential user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userCredentialRepository.save(user);

        // Send HTML password changed email (non-blocking)
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            emailPublisher.publishHtmlEmailMessage(
                    user.getEmail(),
                    "Password Changed Successfully - eBook Store",
                    "password-changed-email",
                    Map.of(
                            "phoneNumber", phoneNumber.toString(),
                            "timestamp", LocalDateTime.now().format(formatter)
                    )
            );
        } catch (Exception e) {
            System.err.println("Failed to send password changed email: " + e.getMessage());
        }

        return "Password updated successfully";
    }
}
