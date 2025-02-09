package com.example.carebridge.mapper;

import com.example.carebridge.dto.ExaminationScheduleDto;
import com.example.carebridge.entity.ExaminationSchedule;
import org.mapstruct.Mapper;

/**
 * ExaminationSchedule 엔티티와 DTO 간의 변환을 처리하는 매퍼 인터페이스
 * MapStruct를 사용하여 자동으로 변환 구현체를 생성합니다.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ExaminationScheduleMapper {
    
    /**
     * ExaminationSchedule 엔티티를 ExaminationScheduleDto로 변환합니다.
     *
     * @param schedule 변환할 ExaminationSchedule 엔티티
     * @return 변환된 ExaminationScheduleDto 객체
     * @throws IllegalArgumentException 엔티티가 null인 경우
     */
    ExaminationScheduleDto toDto(ExaminationSchedule schedule);
    
    /**
     * ExaminationScheduleDto를 ExaminationSchedule 엔티티로 변환합니다.
     *
     * @param scheduleDto 변환할 ExaminationScheduleDto
     * @return 변환된 ExaminationSchedule 엔티티
     * @throws IllegalArgumentException DTO가 null인 경우
     */
    ExaminationSchedule toEntity(ExaminationScheduleDto scheduleDto);
}