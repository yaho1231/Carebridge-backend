// ExaminationScheduleRepository.java
package com.example.carebridge.repository;

import com.example.carebridge.entity.ExaminationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationScheduleRepository extends JpaRepository<ExaminationSchedule, Integer> {

    // 환자 전화번호로 스케줄 조회 메서드
    List<ExaminationSchedule> findByPatientPhone(String patientPhone);

}
