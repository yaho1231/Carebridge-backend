package com.example.carebridge.service;

import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.entity.UserAccount;
import com.example.carebridge.mapper.PatientMapper;
import com.example.carebridge.repository.MedicalStaffRepository;
import com.example.carebridge.repository.PatientRepository;
import com.example.carebridge.repository.UserAccountRepository;
import com.example.carebridge.entity.MedicalStaff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * PatientService 클래스에 대한 단위 테스트
 * 환자 정보 관리의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("환자 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicalStaffRepository medicalStaffRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientService patientService;

    // 공통으로 사용되는 테스트 데이터
    private Integer validPatientId;
    private String validPhoneNumber;
    private String validEmail;
    private String validName;
    private Integer validHospitalId;
    private String validDepartment;
    private Integer validMedicalStaffId;
    private Patient validPatient;
    private PatientDto validPatientDto;
    private MedicalStaff validMedicalStaff;
    private UserAccount validUserAccount;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validPatientId = 1;
        validPhoneNumber = "01012345678";
        validEmail = "patient@example.com";
        validName = "홍길동";
        validHospitalId = 1;
        validDepartment = "내과";
        validMedicalStaffId = 1;

        // 유효한 환자 엔티티 생성
        validPatient = new Patient();
        validPatient.setPatientId(validPatientId);
        validPatient.setPhoneNumber(validPhoneNumber);
        validPatient.setEmail(validEmail);
        validPatient.setName(validName);
        validPatient.setHospitalId(validHospitalId);
        validPatient.setDepartment(validDepartment);
        validPatient.setBirthDate(LocalDateTime.parse("2000-01-01T00:00:00"));
        validPatient.setHospitalizationDate(LocalDateTime.parse("2023-01-01T00:00:00"));
        validPatient.setGender(Patient.Gender.Male);

        // 유효한 환자 DTO 생성
        validPatientDto = new PatientDto();
        validPatientDto.setPatientId(validPatientId);
        validPatientDto.setPhoneNumber(validPhoneNumber);
        validPatientDto.setEmail(validEmail);
        validPatientDto.setName(validName);
        validPatientDto.setHospitalId(validHospitalId);
        validPatientDto.setDepartment(validDepartment);
        validPatientDto.setBirthDate(LocalDateTime.parse("2000-01-01T00:00:00"));
        validPatientDto.setHospitalizationDate(LocalDateTime.parse("2023-01-01T00:00:00"));
        validPatientDto.setGender(Patient.Gender.Male);

        // 유효한 의료진 생성
        validMedicalStaff = MedicalStaff.builder()
                .department(validDepartment)
                .hospitalId(validHospitalId)
                .build();

        // 유효한 사용자 계정 생성
        validUserAccount = new UserAccount();
        validUserAccount.setPhoneNumber(validPhoneNumber);
        validUserAccount.setEmail(validEmail);
        validUserAccount.setName(validName);
    }

    @Nested
    @DisplayName("getPatientList 메소드 테스트")
    class GetPatientListTest {

        @Test
        @DisplayName("의료진 ID로 환자 목록 조회 성공 테스트")
        void getPatientList_Success() {
            // given
            List<Patient> patients = new ArrayList<>();
            patients.add(validPatient);

            when(medicalStaffRepository.findByMedicalStaffId(validMedicalStaffId)).thenReturn(Optional.of(validMedicalStaff));
            when(patientRepository.findByHospitalIdAndDepartment(validHospitalId, validDepartment)).thenReturn(Optional.of(patients));
            when(patientMapper.toDto(any(Patient.class))).thenReturn(validPatientDto);

            // when
            List<PatientDto> result = patientService.getPatientList(validMedicalStaffId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validPatientId, result.get(0).getPatientId());
            assertEquals(validPhoneNumber, result.get(0).getPhoneNumber());
            assertEquals(validEmail, result.get(0).getEmail());
            assertEquals(validName, result.get(0).getName());
            assertEquals(validHospitalId, result.get(0).getHospitalId());
            assertEquals(validDepartment, result.get(0).getDepartment());

            verify(medicalStaffRepository, times(1)).findByMedicalStaffId(validMedicalStaffId);
            verify(patientRepository, times(1)).findByHospitalIdAndDepartment(validHospitalId, validDepartment);
        }

        @Test
        @DisplayName("존재하지 않는 의료진으로 환자 목록 조회 시 예외 발생 테스트")
        void getPatientList_NotFound_MedicalStaff() {
            // given
            when(medicalStaffRepository.findByMedicalStaffId(validMedicalStaffId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.getPatientList(validMedicalStaffId));
            verify(medicalStaffRepository, times(1)).findByMedicalStaffId(validMedicalStaffId);
            verify(patientRepository, never()).findByHospitalIdAndDepartment(anyInt(), anyString());
        }

        @Test
        @DisplayName("해당 병원과 부서에 환자가 없을 때 예외 발생 테스트")
        void getPatientList_NoPatients() {
            // given
            when(medicalStaffRepository.findByMedicalStaffId(validMedicalStaffId)).thenReturn(Optional.of(validMedicalStaff));
            when(patientRepository.findByHospitalIdAndDepartment(validHospitalId, validDepartment)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.getPatientList(validMedicalStaffId));
            verify(medicalStaffRepository, times(1)).findByMedicalStaffId(validMedicalStaffId);
            verify(patientRepository, times(1)).findByHospitalIdAndDepartment(validHospitalId, validDepartment);
        }

        @Test
        @DisplayName("null 의료진 ID로 조회 시 예외 발생 테스트")
        void getPatientList_NullMedicalStaffId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientList(null));
            verify(medicalStaffRepository, never()).findByMedicalStaffId(anyInt());
            verify(patientRepository, never()).findByHospitalIdAndDepartment(anyInt(), anyString());
        }
    }

    @Nested
    @DisplayName("getPatientById 메소드 테스트")
    class GetPatientByIdTest {

        @Test
        @DisplayName("환자 ID로 환자 조회 성공 테스트")
        void getPatientById_Success() {
            // given
            when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(validPatient));

            // when
            Patient result = patientService.getPatientById(validPatientId);

            // then
            assertNotNull(result);
            assertEquals(validPatientId, result.getPatientId());
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            assertEquals(validEmail, result.getEmail());
            assertEquals(validName, result.getName());
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("존재하지 않는 환자 ID로 조회 시 예외 발생 테스트")
        void getPatientById_NotFound() {
            // given
            when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.getPatientById(validPatientId));
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("유효하지 않은 환자 ID로 조회 시 예외 발생 테스트")
        void getPatientById_InvalidId() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientById(0));
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientById(-1));
            verify(patientRepository, never()).findByPatientId(anyInt());
        }
    }

    @Nested
    @DisplayName("getPatientByPhone 메소드 테스트")
    class GetPatientByPhoneTest {

        @Test
        @DisplayName("전화번호로 환자 조회 성공 테스트")
        void getPatientByPhone_Success() {
            // given
            when(patientRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validPatient));

            // when
            Patient result = patientService.getPatientByPhone(validPhoneNumber);

            // then
            assertNotNull(result);
            assertEquals(validPatientId, result.getPatientId());
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            assertEquals(validEmail, result.getEmail());
            assertEquals(validName, result.getName());
            verify(patientRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("존재하지 않는 전화번호로 조회 시 예외 발생 테스트")
        void getPatientByPhone_NotFound() {
            // given
            when(patientRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.getPatientByPhone(validPhoneNumber));
            verify(patientRepository, times(1)).findByPhoneNumber(validPhoneNumber);
        }

        @Test
        @DisplayName("빈 전화번호로 조회 시 예외 발생 테스트")
        void getPatientByPhone_EmptyPhone() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientByPhone(""));
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientByPhone(null));
            verify(patientRepository, never()).findByPhoneNumber(anyString());
        }
    }

    @Nested
    @DisplayName("getPatientByEmail 메소드 테스트")
    class GetPatientByEmailTest {

        @Test
        @DisplayName("이메일로 환자 조회 성공 테스트")
        void getPatientByEmail_Success() {
            // given
            when(patientRepository.findByEmail(validEmail)).thenReturn(Optional.of(validPatient));

            // when
            Patient result = patientService.getPatientByEmail(validEmail);

            // then
            assertNotNull(result);
            assertEquals(validPatientId, result.getPatientId());
            assertEquals(validPhoneNumber, result.getPhoneNumber());
            assertEquals(validEmail, result.getEmail());
            assertEquals(validName, result.getName());
            verify(patientRepository, times(1)).findByEmail(validEmail);
        }

        @Test
        @DisplayName("존재하지 않는 이메일로 조회 시 예외 발생 테스트")
        void getPatientByEmail_NotFound() {
            // given
            when(patientRepository.findByEmail(validEmail)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.getPatientByEmail(validEmail));
            verify(patientRepository, times(1)).findByEmail(validEmail);
        }

        @Test
        @DisplayName("빈 이메일로 조회 시 예외 발생 테스트")
        void getPatientByEmail_EmptyEmail() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientByEmail(""));
            assertThrows(IllegalArgumentException.class, () -> patientService.getPatientByEmail(null));
            verify(patientRepository, never()).findByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("createPatient 메소드 테스트")
    class CreatePatientTest {

        @Test
        @DisplayName("환자 생성 성공 테스트")
        void createPatient_Success() {
            // given
            lenient().when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.of(validUserAccount));
            
            // 저장할 때 Patient의 update 메소드가 호출되므로, mock 필요 없음
            lenient().when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
                Patient patient = invocation.getArgument(0);
                // 테스트를 위해 필요한 필드 설정
                patient.setPatientId(validPatientId);
                return patient;
            });

            // when
            Patient result = patientService.createPatient(validPatient);

            // then
            assertNotNull(result);
            assertEquals(validPatientId, result.getPatientId());
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(patientRepository, times(1)).save(any(Patient.class));
        }

        @Test
        @DisplayName("사용자 계정이 없는 환자 생성 시 예외 발생 테스트")
        void createPatient_NoUserAccount() {
            // given
            lenient().when(userAccountRepository.findByPhoneNumber(validPhoneNumber)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.createPatient(validPatient));
            verify(userAccountRepository, times(1)).findByPhoneNumber(validPhoneNumber);
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("null 환자 정보로 생성 시 예외 발생 테스트")
        void createPatient_NullPatientDto() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> patientService.createPatient(null));
            verify(userAccountRepository, never()).findByPhoneNumber(anyString());
            verify(patientRepository, never()).save(any(Patient.class));
        }
    }

    @Nested
    @DisplayName("isChatRoomExist 메소드 테스트")
    class IsChatRoomExistTest {

        @Test
        @DisplayName("채팅방 존재 확인 테스트 - 환자에게 채팅방 있음")
        void isChatRoomExist_Exists() {
            // given
            Patient patientWithChatRoom = new Patient();
            patientWithChatRoom.setPatientId(validPatientId);
            patientWithChatRoom.setChatRoomId("chat-123");

            when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(patientWithChatRoom));

            // when
            Boolean result = patientService.isChatRoomExist(validPatientId);

            // then
            assertTrue(result);
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("채팅방 존재 확인 테스트 - 환자에게 채팅방 없음")
        void isChatRoomExist_NotExists() {
            // given
            Patient patientWithoutChatRoom = new Patient();
            patientWithoutChatRoom.setPatientId(validPatientId);
            patientWithoutChatRoom.setChatRoomId(null);

            when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(patientWithoutChatRoom));

            // when
            Boolean result = patientService.isChatRoomExist(validPatientId);

            // then
            assertFalse(result);
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
        }

        @Test
        @DisplayName("채팅방 존재 확인 테스트 - 환자가 존재하지 않음")
        void isChatRoomExist_PatientNotFound() {
            // given
            when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.isChatRoomExist(validPatientId));
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
        }
    }

    @Nested
    @DisplayName("updatePhoneNumber 메소드 테스트")
    class UpdatePhoneNumberTest {

        @Test
        @DisplayName("환자 전화번호 업데이트 성공 테스트")
        void updatePhoneNumber_Success() {
            // given
            String newPhoneNumber = "01098765432";
            lenient().when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.of(validPatient));

            // when
            patientService.updatePhoneNumber(validPatientId, newPhoneNumber);

            // then
            assertEquals(newPhoneNumber, validPatient.getPhoneNumber());
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
            verify(patientRepository, times(1)).save(validPatient);
        }

        @Test
        @DisplayName("존재하지 않는 환자 전화번호 업데이트 시 예외 발생 테스트")
        void updatePhoneNumber_PatientNotFound() {
            // given
            String newPhoneNumber = "01098765432";
            lenient().when(patientRepository.findByPatientId(validPatientId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> patientService.updatePhoneNumber(validPatientId, newPhoneNumber));
            verify(patientRepository, times(1)).findByPatientId(validPatientId);
            verify(patientRepository, never()).save(any(Patient.class));
        }

        @Test
        @DisplayName("유효하지 않은 전화번호로 업데이트 시 예외 발생 테스트")
        void updatePhoneNumber_InvalidPhoneNumber() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> patientService.updatePhoneNumber(validPatientId, ""));
            assertThrows(IllegalArgumentException.class, () -> patientService.updatePhoneNumber(validPatientId, null));
            verify(patientRepository, never()).findByPatientId(anyInt());
            verify(patientRepository, never()).save(any(Patient.class));
        }
    }
} 