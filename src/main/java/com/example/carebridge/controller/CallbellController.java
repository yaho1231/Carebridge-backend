package com.example.carebridge.controller;

import com.example.carebridge.entity.ChatRoom;
import com.example.carebridge.entity.Patient;
import com.example.carebridge.repository.ChatRoomRepository;
import com.example.carebridge.repository.MessageRepository;
import com.example.carebridge.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/callbell")
public class CallbellController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MessageRepository messageRepository;

    // 새로운 세션 생성
    @PostMapping("/start-session/{patientId}")
    public Conversation startSession(@PathVariable Integer patientId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            if (patient.getConversation() == null) {
                Conversation conversation = new Conversation();
                conversation.setPatient(patient);
                // 필요한 경우 medicalStaff 설정
                conversation = conversationRepository.save(conversation);
                patient.setConversation(conversation);
                patientRepository.save(patient);
                return conversation;
            } else {
                return patient.getConversation();
            }
        }
        throw new RuntimeException("Patient not found");
    }

    // 환자 ID로 메시지 반환
    @GetMapping("/messages/{patientId}")
    public List<String> getMessages(@PathVariable Integer patientId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            if (patient.getConversation() != null) {
                return messageRepository.findMessageContentByPatientId(patientId);
            }
        }
        throw new RuntimeException("No conversation found for patient");
    }
}