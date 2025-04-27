package com.example.carebridge.repository;

import com.example.carebridge.entity.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, String> {

    /**
     * 전화번호로 보호자 정보를 삭제합니다.
     *
     * @param phoneNumber 보호자 전화번호
     */
    @Modifying
    @Query("DELETE FROM Guardian g WHERE g.phoneNumber = :phoneNumber")
    void deleteByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 전화번호로 보호자 정보를 조회합니다.
     *
     * @param phoneNumber 보호자 전화번호
     * @return 보호자 Optional 엔티티
     */
    @NonNull
    @Query("SELECT g FROM Guardian g WHERE g.phoneNumber = :phoneNumber")
    Optional<Guardian> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 환자 ID로 모든 보호자 정보를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 보호자 엔티티 리스트
     */
    @NonNull
    @Query("SELECT g FROM Guardian g WHERE g.patientId = :patientId ORDER BY g.name ASC")
    Optional<List<Guardian>> findAllByPatientId(@Param("patientId") Integer patientId);

    /**
     * 전화번호로 보호자 존재 여부를 확인합니다.
     *
     * @param phoneNumber 보호자 전화번호
     * @return 존재 여부
     */
    @Query("SELECT COUNT(g) > 0 FROM Guardian g WHERE g.phoneNumber = :phoneNumber")
    boolean existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 환자 ID로 보호자 수를 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 보호자 수
     */
    @Query("SELECT COUNT(g) FROM Guardian g WHERE g.patientId = :patientId")
    long countByPatientId(@Param("patientId") Integer patientId);

    /**
     * 환자 ID와 이름으로 보호자를 검색합니다.
     *
     * @param patientId 환자 ID
     * @param name 보호자 이름
     * @return 보호자 엔티티 리스트
     */
    @NonNull
    @Query("SELECT g FROM Guardian g WHERE g.patientId = :patientId " +
           "AND g.name LIKE %:name% ORDER BY g.name ASC")
    Optional<List<Guardian>> searchByPatientIdAndName(
            @Param("patientId") Integer patientId,
            @Param("name") String name);

    /**
     * 여러 환자 ID로 보호자 목록을 조회합니다.
     *
     * @param patientIds 환자 ID 리스트
     * @return 보호자 엔티티 리스트
     */
    @NonNull
    @Query("SELECT g FROM Guardian g WHERE g.patientId IN :patientIds ORDER BY g.patientId, g.name")
    Optional<List<Guardian>> findByPatientIdIn(@Param("patientIds") List<Integer> patientIds);
}