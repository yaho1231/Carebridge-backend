package com.example.carebridge.controller;

import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.service.HospitalInformationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital-info")
public class HospitalInformationController {

    private final HospitalInformationService hospitalInformationService;

    public HospitalInformationController(HospitalInformationService hospitalInformationService){
        this.hospitalInformationService = hospitalInformationService;
    }

    @GetMapping("/search/{hospital_id}")
    public HospitalInformation searchHospitalInformation(@RequestParam String prompt, @PathVariable int hospital_id){
//        System.out.println(prompt);
        return hospitalInformationService.findMostSimilarHospitalInformation(prompt, hospital_id);
    }

    //아래 내용은 후에 Hospital -> HospitalInfo 등으로 수정
    //더미데이터
    @GetMapping
    public ResponseEntity<List<Hospital>> getHospitals() {return null;}

    //더미데이터
    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable int id) {return null;}

    //더미데이터
    @PostMapping("hospitalInfo")
    public ResponseEntity<Hospital> addHospital(@RequestBody Hospital hospital) {
        return null;
    }

    @PutMapping("hospitalInfo")
    public ResponseEntity<Hospital> updateHospital(@RequestBody Hospital hospital) {
        return null;
    }

    @DeleteMapping("hospitalInfo")
    public ResponseEntity<Hospital> deleteHospital(@RequestBody Hospital hospital) {
        return null;
    }

    @GetMapping("hospitalInfo/search")
    public ResponseEntity<Hospital> getHospitalData(@RequestBody Hospital hospital) {
        return null;
    }
}
