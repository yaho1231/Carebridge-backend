package com.example.carebridge.repository;

import com.example.carebridge.entity.ExaminationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationScheduleRepository extends JpaRepository<ExaminationSchedule, Integer> {

    /**
     * 환자의 전화번호로 검사 일정을 조회합니다.
     * @param patientPhone 환자의 전화번호
     * @return 환자의 검사 일정 목록
     */
    List<ExaminationSchedule> findByPatientPhone(String patientPhone);

}