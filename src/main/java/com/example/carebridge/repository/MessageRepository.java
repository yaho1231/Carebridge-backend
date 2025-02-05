package com.example.carebridge.repository;

import com.example.carebridge.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 메시지 관리를 위한 레포지토리 인터페이스
 * 메시지의 조회 및 관리 기능을 제공합니다.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    /**
     * 환자 ID로 메시지 내용을 조회합니다.
     * 메시지 시간 순으로 정렬하여 반환합니다.
     *
     * @param patientId 환자의 ID
     * @return 환자의 메시지 목록
     */
    @Query("SELECT m FROM Message m WHERE m.patientId = :patientId ORDER BY m.timestamp DESC")
    List<Message> findMessageContentByPatientId(@Param("patientId") Integer patientId);

    /**
     * 모든 메시지를 시간 순으로 조회합니다.
     *
     * @return 정렬된 메시지 목록
     */
    @NonNull
    @Query("SELECT m FROM Message m ORDER BY m.timestamp DESC")
    List<Message> findAll();

    /**
     * 메시지 ID로 메시지를 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     *
     * @param messageId 메시지 ID
     * @return 메시지 정보를 담은 Optional 객체
     */
    @Query("SELECT m FROM Message m WHERE m.messageId = :messageId")
    Optional<Message> findByMessageId(@Param("messageId") Integer messageId);

    /**
     * 의료진 ID로 메시지를 조회합니다.
     * 메시지 시간 순으로 정렬하여 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 의료진의 메시지 목록
     */
    @Query("SELECT m FROM Message m WHERE m.medicalStaffId = :medicalStaffId ORDER BY m.timestamp DESC")
    List<Message> findByMedicalStaffId(@Param("medicalStaffId") Integer medicalStaffId);
}