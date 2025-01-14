package com.example.carebridge.service;

import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.repository.HospitalInformationRepository;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;


@Service
public class HospitalInformationService {
    private final HospitalInformationRepository hospitalInformationRepository;

    public HospitalInformationService(HospitalInformationRepository hospitalInformationRepository) {
        this.hospitalInformationRepository = hospitalInformationRepository;
    }

    public HospitalInformation findMostSimilarHospitalInformation(String prompt, int hospital_id){
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        double maxSimilarity = -1;
        HospitalInformation mostSimilarInfo = null;
        for (HospitalInformation hospitalInformation : hospitalInformationRepository.findAllByHospitalId(hospital_id)){
//        for (HospitalInformation hospitalInformation : hospitalInformationRepository.findAll()) {
            double similarity = cosineSimilarity.cosineSimilarity(
                    Arrays.stream(prompt.split(" "))
                            .collect(Collectors.toMap(word -> word, word -> 1)),
                    Arrays.stream(hospitalInformation.getInformation().split(" "))
                            .collect(Collectors.toMap(word -> word, word -> 1))
            );
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarInfo= hospitalInformation;
//                System.out.println(hospitalInformation.getInformation());
            }
        }
        return mostSimilarInfo;
    }
}
