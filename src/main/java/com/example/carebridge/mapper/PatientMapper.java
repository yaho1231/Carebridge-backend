package com.example.carebridge.mapper;

import com.example.carebridge.dto.PatientDto;
import com.example.carebridge.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Patient 엔티티와 DTO 간의 변환을 처리하는 매퍼
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {
    
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    /**
     * Patient 엔티티를 PatientDto 로 변환합니다.
     *
     * @param patient 변환할 Patient 엔티티
     * @return 변환된 PatientDto 객체
     */
    PatientDto toDto(Patient patient);

    /**
     * PatientDto 를 Patient 엔티티로 변환합니다.
     *
     * @param patientDto 변환할 PatientDto
     * @return 변환된 Patient 엔티티
     */
    Patient toEntity(PatientDto patientDto);
}
