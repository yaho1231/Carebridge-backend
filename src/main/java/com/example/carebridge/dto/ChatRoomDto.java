package com.example.carebridge.dto;

import com.example.carebridge.service.MedicalStaffService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChatRoomDto {
    private MedicalStaffService medicalStaffService;
    /**
     * 채팅방의 고유 ID
     */
    private String roomId;

    private Integer patientId;

    private Integer medicalStaffId;
}