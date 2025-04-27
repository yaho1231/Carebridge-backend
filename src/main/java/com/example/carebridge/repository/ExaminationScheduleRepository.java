package com.example.carebridge.repository;

import com.example.carebridge.entity.ExaminationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationScheduleRepository extends JpaRepository<ExaminationSchedule, Integer> {

    /**
     * 환자의 ID로 검사 일정을 조회합니다.
     *
     * @param patientId 환자 ID
     * @return 환자의 검사 일정 목록
     */
    @NonNull
    @Query("SELECT e FROM ExaminationSchedule e WHERE e.patientId = :patientId ORDER BY e.scheduleDate ASC")
    Optional<List<ExaminationSchedule>> findByPatientId(@Param("patientId") Integer patientId);

    /**
     * 의료진 ID로 검사 일정을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 의료진의 검사 일정 목록
     */
    @NonNull
    @Query("SELECT e FROM ExaminationSchedule e WHERE e.medicalStaffId = :medicalStaffId ORDER BY e.scheduleDate ASC")
    Optional<List<ExaminationSchedule>> findByMedicalStaffId(@Param("medicalStaffId") Integer medicalStaffId);

    @Query("SELECT e FROM ExaminationSchedule e WHERE DATE(e.scheduleDate) = CURRENT_DATE AND e.patientId = :patientId ORDER BY e.scheduleDate ASC")
    Optional<List<ExaminationSchedule>> findTodaySchedulesByPatientId(@Param("patientId") Integer patientId);

    @Query("SELECT e FROM ExaminationSchedule e WHERE e.id = :id")
    Optional<ExaminationSchedule> findById(@Param("id") int id);
}