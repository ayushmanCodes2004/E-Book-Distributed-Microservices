package com.ebook.Auth_Service.Controller;

import com.ebook.Auth_Service.DTO.OtpRequestDTO;
import com.ebook.Auth_Service.DTO.PasswordResetDTO;
import com.ebook.Auth_Service.DTO.UserDTO;
import com.ebook.Auth_Service.DTO.UserResponseDTO;
import com.ebook.Auth_Service.Entity.UserCredential;
import com.ebook.Auth_Service.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO user){
        return new ResponseEntity<>(authService.registerUser(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserDTO user){
        Authentication authenticatedUser = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
        );
        String generatedJwtToken = authService.generateToken(authenticatedUser.getName());
        return new ResponseEntity<>(generatedJwtToken, HttpStatus.OK);
    }

    @GetMapping("/validateCustomerId/{customerId}")
    public UserResponseDTO validCustomerId(@PathVariable String customerId) {
        return authService.validCustomerId(customerId);
    }


    @PostMapping("/password/request-otp")
    public ResponseEntity<String> requestPasswordResetOtp(@RequestBody OtpRequestDTO request) {
        String response = authService.requestPasswordResetOtp(request.getPhoneNumber());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO request) {
        String response = authService.resetPassword(
            request.getPhoneNumber(),
            request.getOtp(),
            request.getNewPassword()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


   




}
