package com.example.carebridge.mapper;

import com.example.carebridge.dto.GuardianDto;
import com.example.carebridge.entity.Guardian;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

/**
 * Guardian 엔티티와 GuardianDto 간의 변환을 처리하는 매퍼 인터페이스
 * MapStruct 를 사용하여 자동으로 변환 구현체를 생성합니다.
 */
@Component
@Mapper(componentModel = "spring")
public interface GuardianMapper {
    
    /**
     * Guardian 엔티티를 GuardianDto 로 변환합니다.
     *
     * @param guardian 변환할 Guardian 엔티티
     * @return 변환된 GuardianDto 객체
     * @throws IllegalArgumentException 엔티티가 null 인 경우
     */
    GuardianDto toDto(Guardian guardian);
    
    /**
     * GuardianDto 를 Guardian 엔티티로 변환합니다.
     *
     * @param guardianDto 변환할 GuardianDto
     * @return 변환된 Guardian 엔티티
     * @throws IllegalArgumentException DTO 가 null 인 경우
     */
    Guardian toEntity(GuardianDto guardianDto);

    /**
     * 기존 Guardian 엔티티를 업데이트합니다.
     *
     * @param guardianDto 업데이트할 정보가 담긴 DTO
     * @param guardian 업데이트할 대상 엔티티
     * @throws IllegalArgumentException DTO 또는 엔티티가 null 인 경우
     */
    void updateFromDto(GuardianDto guardianDto, @MappingTarget Guardian guardian);
} 