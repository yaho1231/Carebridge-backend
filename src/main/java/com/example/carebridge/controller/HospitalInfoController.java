package com.example.carebridge.controller;

import com.example.carebridge.entity.HospitalInfo;
import com.example.carebridge.service.HospitalInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HospitalInfoController {

    private final HospitalInfoService hospitalInfoService;

    public HospitalInfoController(HospitalInfoService hospitalInfoService) {
        this.hospitalInfoService = hospitalInfoService;
    }

    @PostMapping("/hospital-info")
    public ResponseEntity<String> saveHospitalInfo(@RequestBody HospitalInfo hospitalInfo) {
        hospitalInfoService.saveHospitalInfo(hospitalInfo);
        return ResponseEntity.ok("Information saved successfully.");
    }
}