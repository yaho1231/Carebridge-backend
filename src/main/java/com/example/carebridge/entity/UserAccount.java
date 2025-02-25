package com.example.carebridge.entity;

import com.example.carebridge.dto.UserAccountDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "User_Account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    @Column(name = "user_id") // user_id column mapping
    private Integer id; // User unique ID

//    @OneToOne(mappedBy = "userAccount", fetch = FetchType.LAZY)
//    private Patient patient;  // Patient와의 1:1 관계

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    @Column(nullable = false) // Not Null 제약 조건 설정
    private UserAccount.Gender gender; // 환자 성별 (Male/Female)

    @Column(name = "email")
    private String email;

    @Column(name = "otp")
    private String otp; // 임시 저장

    @Column(name = "otp_expiry")
    private LocalDateTime otpExpiry; // OTP 만료 시간

    @Column(name = "fcm_token")
    private String fcmToken; // FCM 토큰

    public enum Gender {
        Male, Female
    }

    public UserAccount() {}

    public void update(UserAccountDto userAccountDto) {
        this.name = userAccountDto.getName();
        this.phoneNumber = userAccountDto.getPhoneNumber();
        this.birthDate = userAccountDto.getBirthDate();
        this.gender = userAccountDto.getGender();
        this.email = userAccountDto.getEmail();
    }
}
