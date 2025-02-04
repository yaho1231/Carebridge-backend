package com.example.carebridge.controller;

import com.example.carebridge.repository.HospitalRepository;
import com.example.carebridge.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;


    public HospitalController(HospitalRepository hospitalRepository, HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/name/{hospital_id}")
    @ResponseBody
    private ResponseEntity<String> getHospitalName(@PathVariable Integer hospital_id) {
        return ResponseEntity.ok(hospitalService.getHospitalName(hospital_id));
    }

}
