package com.example.carebridge.repository;

import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.entity.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.lang.NonNull;

@Repository
public interface ExaminationScheduleRepository extends JpaRepository<ExaminationSchedule, Integer> {

    /**
     * 환자의 ID로 검사 일정을 조회합니다.
     * 
     * @param patient 환자 정보
     * @return 환자의 검사 일정 목록
     */
    @NonNull
    @Query("SELECT e FROM ExaminationSchedule e WHERE e.patientId = :patient " +
           "AND e.scheduleDate >= CURRENT_TIMESTAMP " +
           "ORDER BY e.scheduleDate ASC")
    List<ExaminationSchedule> findByPatientId(@Param("patient") Patient patient);

    /**
     * 의료진 ID로 검사 일정을 조회합니다.
     *
     * @param medicalStaff 의료진 정보
     * @return 의료진의 검사 일정 목록
     */
    @NonNull
    @Query("SELECT e FROM ExaminationSchedule e WHERE e.medicalStaffId = :medicalStaff " +
           "AND e.scheduleDate >= CURRENT_TIMESTAMP " +
           "ORDER BY e.scheduleDate ASC")
    List<ExaminationSchedule> findByMedicalStaffId(@Param("medicalStaff") MedicalStaff medicalStaff);

    /**
     * 특정 기간 내의 검사 일정을 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 검사 일정 목록
     */
    @NonNull
    @Query("SELECT e FROM ExaminationSchedule e WHERE e.scheduleDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.scheduleDate ASC")
    List<ExaminationSchedule> findByScheduleDateBetween(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 시간에 의료진의 일정 중복을 확인합니다.
     *
     * @param medicalStaff 의료진 정보
     * @param scheduleDate 예약하려는 시간
     * @return 중복된 일정의 수
     */
    @Query("SELECT COUNT(e) FROM ExaminationSchedule e WHERE e.medicalStaffId = :medicalStaff " +
           "AND e.scheduleDate = :scheduleDate")
    long countConflictingSchedules(
            @Param("medicalStaff") MedicalStaff medicalStaff,
            @Param("scheduleDate") LocalDateTime scheduleDate);

    /**
     * 환자의 지난 검사 일정을 조회합니다.
     *
     * @param patient 환자 정보
     * @return 환자의 지난 검사 일정 목록
     */
    @NonNull
    @Query("SELECT e FROM ExaminationSchedule e WHERE e.patientId = :patient " +
           "AND e.scheduleDate < CURRENT_TIMESTAMP " +
           "ORDER BY e.scheduleDate DESC")
    List<ExaminationSchedule> findPastSchedulesByPatient(@Param("patient") Patient patient);
}