package com.example.carebridge.mapper;

import com.example.carebridge.dto.ChatRoomDto;
import com.example.carebridge.entity.ChatRoom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * ChatRoom 엔티티와 ChatRoomDto 간의 변환을 처리하는 매퍼 인터페이스
 * MapStruct 를 사용하여 자동으로 변환 구현체를 생성합니다.
 */
@Mapper(componentModel = "spring")
@Tag(name = "ChatRoom Mapper", description = "채팅방 엔티티-DTO 변환 매퍼")
public interface ChatRoomMapper {
    
    /**
     * ChatRoom 엔티티를 ChatRoomDto 로 변환합니다.
     *
     * @param chatRoom 변환할 ChatRoom 엔티티
     * @return 변환된 ChatRoomDto 객체
     * @throws IllegalArgumentException 엔티티가 null 인 경우
     */
    @Operation(summary = "엔티티를 DTO 로 변환", description = "ChatRoom 엔티티를 ChatRoomDto 로 변환합니다.")
    @Mapping(source = "chatRoomId", target = "roomId")
    ChatRoomDto toDto(ChatRoom chatRoom);
    
    /**
     * ChatRoomDto 를 ChatRoom 엔티티로 변환합니다.
     *
     * @param chatRoomDto 변환할 ChatRoomDto
     * @return 변환된 ChatRoom 엔티티
     * @throws IllegalArgumentException DTO 가 null 인 경우
     */
    @Operation(summary = "DTO 를 엔티티로 변환", description = "ChatRoomDto 를 ChatRoom 엔티티로 변환합니다.")
    @Mapping(source = "roomId", target = "chatRoomId")
    ChatRoom toEntity(ChatRoomDto chatRoomDto);

    /**
     * 기존 ChatRoom 엔티티를 업데이트합니다.
     *
     * @param chatRoomDto 업데이트할 정보가 담긴 DTO
     * @param chatRoom 업데이트할 대상 엔티티
     * @throws IllegalArgumentException DTO 또는 엔티티가 null 인 경우
     */
    @Operation(summary = "엔티티 업데이트", description = "기존 ChatRoom 엔티티를 DTO 정보로 업데이트합니다.")
    @Mapping(source = "roomId", target = "chatRoomId")
    void updateFromDto(ChatRoomDto chatRoomDto, @MappingTarget ChatRoom chatRoom);

    /**
     * null 체크를 수행하는 기본 메서드
     *
     * @param object 검사할 객체
     * @param message 에러 메시지
     * @throws IllegalArgumentException 객체가 null 인 경우
     */
    default void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
} 