package com.ebook.Auth_Service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_credential", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "userName", name = "uk_username"),
           @UniqueConstraint(columnNames = "email", name = "uk_email"),
           @UniqueConstraint(columnNames = "phoneNumber", name = "uk_phone_number"),
           @UniqueConstraint(columnNames = "customerId", name = "uk_customer_id")
       })
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    
    @Column(nullable = false, unique = true, length = 50)
    private String userName;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, unique = true, length = 20)
    private String customerId;
    
    @Column(nullable = false, unique = true)
    private Long phoneNumber;
}
