package com.example.carebridge.repository;

import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationScheduleRepository extends JpaRepository<ExaminationSchedule, Integer> {

    /**
     * 환자의 ID로 검사 일정을 조회합니다.
     *
     * @param patient 환자의 ID
     * @return 환자의 검사 일정 목록
     */
    List<ExaminationSchedule> findByPatient(Patient patient);

}