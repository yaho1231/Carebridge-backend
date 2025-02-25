package com.example.carebridge.repository;

import com.example.carebridge.entity.Macro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 매크로 관리를 위한 레포지토리 인터페이스
 * 의료진의 매크로 정보를 관리하고 조회하는 기능을 제공합니다.
 */
@Repository
public interface MacroRepository extends JpaRepository<Macro, Integer> {

    /**
     * 특정 의료진의 모든 매크로를 조회합니다.
     * 매크로 이름 기준으로 정렬하여 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 정렬된 매크로 목록
     */
    @Query("SELECT m FROM Macro m WHERE m.medicalStaffId = :medicalStaffId ORDER BY m.macroName")
    List<Macro> findAllByMedicalStaffId(@Param("medicalStaffId") Integer medicalStaffId);

    /**
     * 특정 의료진의 특정 매크로를 조회합니다.
     * Optional을 사용하여 null 안전성을 보장합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param macroName 매크로 이름
     * @return 매크로 정보를 담은 Optional 객체
     */
    @Query("SELECT m FROM Macro m WHERE m.medicalStaffId = :medicalStaffId AND m.macroName = :macroName")
    Optional<Macro> findByMedicalStaffIdAndMacroName(
        @Param("medicalStaffId") Integer medicalStaffId, 
        @Param("macroName") String macroName
    );
}