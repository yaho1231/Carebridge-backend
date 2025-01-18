package com.example.carebridge.repository;

import com.example.carebridge.entity.Message;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /**
     * 환자의 ID로 메시지 내용을 조회합니다.
     * @param patientId 환자의 ID
     * @return 환자의 메시지 내용 목록
     */
    List<Message> findMessageContentByPatientId(Integer patientId);

    /**
     * 모든 메시지를 조회합니다.
     * @return 모든 메시지 목록
     */
    @Nonnull
    List<Message> findAll();

    /**
     * 메시지 ID로 메시지를 조회합니다.
     *
     * @param messageId 메시지 ID
     * @return 메시지 객체
     */
    Message findByMessageId(Integer messageId);

    /**
     * 의료진 ID로 메시지를 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 의료진의 메시지 목록
     */
    List<Message> findByMedicalStaffId(Integer medicalStaffId);
}