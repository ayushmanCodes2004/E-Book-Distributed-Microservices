package com.ebook.Auth_Service.DTO;

import lombok.Data;

@Data
public class UserDTO {

    private String userName;
    private String password;
    private String email;
    private Integer phoneNumber;

}
