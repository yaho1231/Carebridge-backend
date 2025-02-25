package com.example.carebridge.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * ChatRoomDto 는 채팅방의 정보를 담고 있는 데이터 전송 객체입니다.
 */
@Data
@Getter
@Setter
public class ChatRoomDto {
    /**
     * 채팅방의 고유 ID
     */
    private String roomId;

    /**
     * 환자의 ID
     */
    private Integer patientId;

    /**
     * 의료진의 ID
     */
    private Integer medicalStaffId;
}