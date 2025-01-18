package com.example.carebridge.repository;

import com.example.carebridge.entity.Macro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MacroRepository extends JpaRepository<Macro, Integer> {

    /**
     * 특정 의료진의 모든 매크로를 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 매크로 목록
     */
    List<Macro> findAllByMedicalStaffId(Integer medicalStaffId);

    /**
     * 특정 의료진의 특정 매크로를 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param macroName 매크로 이름
     * @return 매크로
     */
    Macro findByMedicalStaffIdAndMacroName(Integer medicalStaffId, String macroName);
}