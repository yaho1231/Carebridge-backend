package com.example.carebridge.repository;

import com.example.carebridge.entity.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffAccountRepository extends JpaRepository<StaffAccount, Long> {
    Optional<StaffAccount> getStaffAccountByUserId(String userId);
}
