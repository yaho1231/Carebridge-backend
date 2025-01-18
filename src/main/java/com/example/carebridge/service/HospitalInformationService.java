package com.example.carebridge.service;

import com.example.carebridge.dto.HospitalInformationDto;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.repository.HospitalInformationRepository;
import org.apache.commons.text.similarity.CosineSimilarity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    /**
     * 특정 병원의 모든 병원 정보를 조회하여 HospitalInformationDto 리스트로 반환합니다.
     *
     * @param hospital_id 병원 ID
     * @return 병원 정보가 담긴 HospitalInformationDto 리스트
     */
    public List<HospitalInformationDto> getHospitalInformationList(int hospital_id) {
        List<HospitalInformation> hospitalInformationList = hospitalInformationRepository.findAllByHospitalId(hospital_id);
        List<HospitalInformationDto> hospitalInformationDtoList = new ArrayList<>();
        HospitalInformationDto hospitalInformationDto = new HospitalInformationDto();
        for (HospitalInformation hospitalInformation : hospitalInformationList) {
            hospitalInformationDto.setId(hospitalInformation.getId());
            hospitalInformationDto.setHospitalId(hospitalInformation.getHospitalId());
            hospitalInformationDto.setInformation(hospitalInformation.getInformation());
            hospitalInformationDto.setTitle(hospitalInformation.getTitle());
            hospitalInformationDtoList.add(hospitalInformationDto);
        }
        return hospitalInformationDtoList;
    }

    /**
     * 특정 병원의 특정 제목을 가진 병원 정보를 조회합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     * @return 병원 정보 내용
     */
    public String getHospitalInformation(Integer hospital_id, String title) {
        HospitalInformation hospitalInformation = hospitalInformationRepository.findByHospitalIdAndTitle(hospital_id, title);
        return hospitalInformation.getInformation();
    }

    /**
     * 새로운 병원 정보를 추가합니다.
     *
     * @param hospital_id 병원 ID
     * @param hospitalInformationDto 병원 정보 DTO
     */
    public void addHospitalInformation(int hospital_id, HospitalInformationDto hospitalInformationDto) {
        HospitalInformation hospitalInformation = new HospitalInformation();
        hospitalInformation.setHospitalId(hospital_id);
        hospitalInformation.setHospitalId(hospitalInformationDto.getHospitalId());
        hospitalInformation.setCategory(hospitalInformationDto.getCategory());
        hospitalInformation.setTitle(hospitalInformationDto.getTitle());
        hospitalInformation.setInformation(hospitalInformationDto.getInformation());
        hospitalInformationRepository.save(hospitalInformation);
    }

    /**
     * 기존 병원 정보를 업데이트합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     * @param information 새로운 정보 내용
     */
    public void updateHospitalInformation(int hospital_id, String title, String information) {
        HospitalInformation hospitalInformation = hospitalInformationRepository.findByHospitalIdAndTitle(hospital_id, title);
        hospitalInformation.setInformation(information);
        hospitalInformationRepository.save(hospitalInformation);
    }

    /**
     * 기존 병원 정보를 삭제합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     */
    public void deleteHospitalInformation(int hospital_id, String title) {
        HospitalInformation hospitalInformation = hospitalInformationRepository.findByHospitalIdAndTitle(hospital_id, title);
        hospitalInformationRepository.delete(hospitalInformation);
    }
}