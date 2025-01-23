package com.example.carebridge.repository;

import com.example.carebridge.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAccountRepository extends JpaRepository<UserAccount, Long> {
}
