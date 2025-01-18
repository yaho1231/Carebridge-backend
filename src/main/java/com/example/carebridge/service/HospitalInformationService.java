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

    // HospitalInformationRepository 를 주입받는 생성자
    public HospitalInformationService(HospitalInformationRepository hospitalInformationRepository) {
        this.hospitalInformationRepository = hospitalInformationRepository;
    }

    /**
     * 주어진 프롬프트와 가장 유사한 병원 정보를 찾습니다.
     *
     * @param prompt 프롬프트 내용
     * @param hospital_id 병원 ID
     * @return 가장 유사한 병원 정보
     */
    public HospitalInformation findMostSimilarHospitalInformation(String prompt, int hospital_id) {
        CosineSimilarity cosineSimilarity = new CosineSimilarity(); // 코사인 유사도 계산을 위한 객체 생성
        double maxSimilarity = -1; // 최대 유사도 초기화
        HospitalInformation mostSimilarInfo = null; // 가장 유사한 병원 정보를 저장할 변수 초기화

        // 주어진 병원 ID에 해당하는 모든 병원 정보를 순회
        for (HospitalInformation hospitalInformation : hospitalInformationRepository.findAllByHospitalId(hospital_id)) {
            // 프롬프트와 병원 정보의 유사도를 계산
            double similarity = cosineSimilarity.cosineSimilarity(
                    Arrays.stream(prompt.split(" "))
                            .collect(Collectors.toMap(word -> word, word -> 1)),
                    Arrays.stream(hospitalInformation.getInformation().split(" "))
                            .collect(Collectors.toMap(word -> word, word -> 1))
            );

            // 현재 유사도가 최대 유사도보다 크면 업데이트
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                mostSimilarInfo = hospitalInformation;
            }
        }
        return mostSimilarInfo; // 가장 유사한 병원 정보 반환
    }
}