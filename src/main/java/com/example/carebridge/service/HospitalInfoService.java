package com.example.carebridge.service;

import com.example.carebridge.entity.HospitalInfo;
import com.example.carebridge.repository.HospitalInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalInfoService {

    private final HospitalInfoRepository hospitalInfoRepository;

    public HospitalInfoService(HospitalInfoRepository hospitalInfoRepository) {
        this.hospitalInfoRepository = hospitalInfoRepository;
    }

    // Get all information for a specific category
    public List<HospitalInfo> getHospitalInfoByCategory(String category) {
        return hospitalInfoRepository.findByCategory(category);
    }

    // Get all information for a specific hospital
    public List<HospitalInfo> getHospitalInfoByHospitalId(Long hospitalId) {
        return hospitalInfoRepository.findByHospitalId(hospitalId);
    }

    // Get all information for a specific category and hospital
    public List<HospitalInfo> getHospitalInfoByCategoryAndHospitalId(String category, Long hospitalId) {
        return hospitalInfoRepository.findByCategoryAndHospitalId(category, hospitalId);
    }

    public void saveHospitalInfo(HospitalInfo hospitalInfo) {
        hospitalInfoRepository.save(hospitalInfo);
    }
}