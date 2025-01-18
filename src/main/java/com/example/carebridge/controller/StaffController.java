package com.example.carebridge.controller;


import com.example.carebridge.entity.ExaminationSchedule;
import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.entity.Patient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Messages;
import retrofit2.http.DELETE;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    public StaffController() {

    }

    //더미데이터
    @PostMapping("login")
    public ResponseEntity<List<MedicalStaff>> login(@RequestBody List<MedicalStaff> medicalStaffs) {
        return ResponseEntity.ok(medicalStaffs);
    }

}
