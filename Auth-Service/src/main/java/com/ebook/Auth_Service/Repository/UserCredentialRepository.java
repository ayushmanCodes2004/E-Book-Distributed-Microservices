package com.ebook.Auth_Service.Repository;

import com.ebook.Auth_Service.Entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential,Integer> {
    Optional<UserCredential> findByUserName(String userName);

    Optional<UserCredential> findByCustomerId(String customerId);

    Optional<UserCredential> findByPhoneNumber(Long phoneNumber);

    Optional<UserCredential> findByEmail(String email);

    boolean existsByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(Long phoneNumber);

}
