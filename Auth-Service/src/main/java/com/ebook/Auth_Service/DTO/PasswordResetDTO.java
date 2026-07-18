package com.ebook.Auth_Service.DTO;

import lombok.Data;

@Data
public class PasswordResetDTO {

    private Integer phoneNumber;
    private String otp;
    private String newPassword;

}
