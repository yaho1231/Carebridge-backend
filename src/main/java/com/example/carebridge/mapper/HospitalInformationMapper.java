package com.example.carebridge.mapper;

import com.example.carebridge.dto.HospitalInformationDto;
import com.example.carebridge.entity.HospitalInformation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * HospitalInformation 엔티티와 DTO 간의 변환을 처리하는 매퍼
 */
@Mapper(componentModel = "spring")
public interface HospitalInformationMapper {
    
    HospitalInformationMapper INSTANCE = Mappers.getMapper(HospitalInformationMapper.class);

    /**
     * HospitalInformation 엔티티를 HospitalInformationDto 로 변환합니다.
     *
     * @param hospitalInformation 변환할 HospitalInformation 엔티티
     * @return 변환된 HospitalInformationDto 객체
     */
    @Mapping(source = "hospital.hospitalId", target = "hospitalId")
    HospitalInformationDto toDto(HospitalInformation hospitalInformation);

    /**
     * HospitalInformationDto 를 HospitalInformation 엔티티로 변환합니다.
     *
     * @param hospitalInformationDto 변환할 HospitalInformationDto
     * @return 변환된 HospitalInformation 엔티티
     */
    @Mapping(target = "hospital", ignore = true)
    HospitalInformation toEntity(HospitalInformationDto hospitalInformationDto);
} 