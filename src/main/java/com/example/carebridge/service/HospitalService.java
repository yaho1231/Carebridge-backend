package com.example.carebridge.service;

import com.example.carebridge.repository.HospitalRepository;
import org.springframework.stereotype.Service;

@Service
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public String getHospitalName(Integer hospital_id) {
        return hospitalRepository.findByHospitalId(hospital_id).getName();
    }
}
