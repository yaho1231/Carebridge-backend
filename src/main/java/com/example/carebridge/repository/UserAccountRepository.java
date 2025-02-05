package com.example.carebridge.repository;

import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByPhoneNumber(String phoneNumber);
    UserAccount findByEmail(String email);
}
