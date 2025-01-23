package com.example.carebridge.controller;


import com.example.carebridge.entity.MedicalStaff;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffAccountController {

    public StaffAccountController() {

    }

    //더미데이터
    @PostMapping("login")
    public ResponseEntity<List<MedicalStaff>> login(@RequestBody List<MedicalStaff> medicalStaffs) {
        return ResponseEntity.ok(medicalStaffs);
    }

}
