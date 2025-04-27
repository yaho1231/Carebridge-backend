package com.example.carebridge.service;

import com.example.carebridge.dto.HospitalInformationDto;
import com.example.carebridge.entity.Hospital;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.repository.HospitalInformationRepository;
import com.example.carebridge.repository.HospitalRepository;
import com.example.carebridge.mapper.HospitalInformationMapper;
import org.apache.commons.text.similarity.CosineSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 병원 정보 관리 서비스
 * 병원의 상세 정보를 관리하고 검색하는 기능을 제공하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class HospitalInformationService {
    private final HospitalInformationRepository hospitalInformationRepository;
    private final HospitalRepository hospitalRepository;
    private final HospitalInformationMapper hospitalInformationMapper;

    /**
     * 필요한 레포지토리들을 주입받는 생성자입니다.
     *
     * @param hospitalInformationRepository 병원 정보 레포지토리
     * @param hospitalRepository 병원 레포지토리
     * @param hospitalInformationMapper 병원 정보 매퍼
     */
    public HospitalInformationService(HospitalInformationRepository hospitalInformationRepository, 
                                    HospitalRepository hospitalRepository,
                                    HospitalInformationMapper hospitalInformationMapper) {
        this.hospitalInformationRepository = hospitalInformationRepository;
        this.hospitalRepository = hospitalRepository;
        this.hospitalInformationMapper = hospitalInformationMapper;
    }

    /**
     * 주어진 프롬프트와 가장 유사한 병원 정보를 찾습니다.
     * 코사인 유사도를 사용하여 텍스트 유사도를 계산합니다.
     *
     * @param prompt 검색할 프롬프트 내용
     * @param hospitalId 병원 ID
     * @return 가장 유사한 병원 정보
     * @throws IllegalArgumentException 병원을 찾을 수 없거나 프롬프트가 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public HospitalInformation findMostSimilarHospitalInformation(String prompt, int hospitalId) {
        log.debug("프롬프트 기반 병원 정보 유사도 검색 시도 - 병원 ID: {}, 프롬프트: {}", hospitalId, prompt);
        try {
            if (prompt == null || prompt.trim().isEmpty()) {
                log.error("프롬프트가 null 이거나 비어있습니다.");
                throw new IllegalArgumentException("프롬프트는 필수 입력값입니다.");
            }
            CosineSimilarity cosineSimilarity = new CosineSimilarity();
            double maxSimilarity = -1;
            HospitalInformation mostSimilarInfo = null;
            List<HospitalInformation> hospitalInfoList = hospitalInformationRepository.findAllByHospitalId(hospitalId)
                    .orElseThrow(() -> {
                        log.error("프롬프트와 유사한 병원정보 목록을 찾을 수 없습니다 - 병원 아이디: {}", hospitalId);
                        return new NoSuchElementException("해당 병원의 정보 목록을 찾을 수 없습니다.");
                    });
            for (HospitalInformation info : hospitalInfoList) {
                double similarity = calculateSimilarity(cosineSimilarity, prompt, info.getInformation());
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    mostSimilarInfo = info;
                }
            }
            log.info("유사도 검색 완료 - 병원 ID: {}, 최대 유사도: {}", hospitalId, maxSimilarity);
            return mostSimilarInfo;
        } catch (IllegalArgumentException e) {
            log.error("병원 정보 유사도 검색 실패 - 병원 ID: {}, 오류: {}", hospitalId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 정보 유사도 검색 실패 - 병원 ID: {}, 오류: {}", hospitalId, e.getMessage());
            throw new NoSuchElementException("해당 병원의 정보가 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("병원 정보 유사도 검색 중 예상치 못한 오류 발생 - 병원 ID: {}, 오류: {}", hospitalId, e.getMessage(), e);
            throw new RuntimeException("병원 정보 유사도 검색에 실패했습니다.", e);
        }
    }

    /**
     * 특정 병원의 모든 정보를 조회합니다.
     *
     * @param hospitalId 병원 ID
     * @return 병원 정보 DTO 리스트
     * @throws IllegalArgumentException 병원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public List<HospitalInformationDto> getHospitalInformationList(int hospitalId) {
        log.debug("병원 정보 목록 조회 시도 - 병원 ID: {}", hospitalId);
        try {
            List<HospitalInformation> infoList = hospitalInformationRepository.findAllByHospitalId(hospitalId)
                    .orElseThrow(() -> {
                        log.error("병원 정보 목록을 찾을 수 없습니다 - 병원 ID: {}", hospitalId);
                        return new NoSuchElementException("해당 병원의 정보 목록을 찾을 수 없습니다.");
                    });
            List<HospitalInformationDto> dtoList = infoList.stream()
                    .map(hospitalInformationMapper::toDto)
                    .collect(Collectors.toList());
            log.info("병원 정보 목록 조회 성공 - 병원 ID: {}, 정보 수: {}", hospitalId, dtoList.size());
            return dtoList;
        } catch (NoSuchElementException e) {
            log.error("병원 정보 목록 조회 실패 - 병원 ID: {}, 오류: {}", hospitalId, e.getMessage());
            throw new NoSuchElementException("해당 병원의 정보 목록이 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("병원 정보 목록 조회 중 예상치 못한 오류 발생 - 병원 ID: {}, 오류: {}", hospitalId, e.getMessage(), e);
            throw new RuntimeException("병원 정보 목록 조회에 실패했습니다.", e);
        }
    }

    /**
     * 특정 병원의 특정 제목을 가진 정보를 조회합니다.
     *
     * @param hospitalId 병원 ID
     * @param title 정보 제목
     * @return 병원 정보 내용
     * @throws IllegalArgumentException 병원이나 정보를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getHospitalInformation(Integer hospitalId, String title) {
        log.debug("병원 정보 조회 시도 - 병원 ID: {}, 제목: {}", hospitalId, title);
        try {
            if (title == null || title.trim().isEmpty()) {
                log.error("제목이 비어있습니다.");
                throw new IllegalArgumentException("제목은 필수 입력값입니다.");
            }
            String information = hospitalInformationRepository.findByHospitalIdAndTitle(hospitalId, title)
                    .map(HospitalInformation::getInformation)
                    .orElseThrow(() -> {
                        log.error("병원 정보를 찾을 수 없습니다 - 병원 ID: {}, 제목: {}", hospitalId, title);
                        return new NoSuchElementException("해당 제목의 병원 정보를 찾을 수 없습니다.");
                    });
            log.info("병원 정보 조회 성공 - 병원 ID: {}, 제목: {}", hospitalId, title);
            return information;
        } catch (IllegalArgumentException e) {
            log.error("병원 정보 조회 실패 - 병원 ID: {}, 제목: {}, 오류: {}", hospitalId, title, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 정보 조회 실패 - 병원 ID: {}, 제목: {}, 오류: {}", hospitalId, title, e.getMessage());
            throw new NoSuchElementException("해당 제목의 병원 정보가 존재하지 않습니다.");
        } catch (Exception e) {
            log.error("병원 정보 조회 중 예상치 못한 오류 발생 - 병원 ID: {}, 제목: {}, 오류: {}", hospitalId, title, e.getMessage(), e);
            throw new RuntimeException("병원 정보 조회에 실패했습니다.", e);
        }
    }

    /**
     * 새로운 병원 정보를 추가합니다.
     *
     * @param hospitalInformationDto 병원 정보 DTO
     * @throws IllegalArgumentException 병원을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addHospitalInformation(HospitalInformationDto hospitalInformationDto) {
        log.debug("병원 정보 추가 시도 - 병원 ID: {}, 제목: {}",
                hospitalInformationDto != null ? hospitalInformationDto.getHospitalId() : "null",
                hospitalInformationDto != null ? hospitalInformationDto.getTitle() : "null");
        try {
            if (hospitalInformationDto == null) {
                log.error("병원 정보 DTO가 null입니다.");
                throw new IllegalArgumentException("병원 정보는 null일 수 없습니다.");
            }
            Hospital hospital = hospitalRepository.findByHospitalId(hospitalInformationDto.getHospitalId())
                    .orElseThrow(() -> {
                        log.error("병원을 찾을 수 없습니다 - 병원 ID: {}", hospitalInformationDto.getHospitalId());
                        return new NoSuchElementException("해당 ID의 병원을 찾을 수 없습니다.");
                    });
            HospitalInformation hospitalInformation = hospitalInformationMapper.toEntity(hospitalInformationDto);
            hospitalInformation.setHospital(hospital);
            hospitalInformationRepository.save(hospitalInformation);
            log.info("새로운 병원 정보 추가 성공 - 병원 ID: {}, 제목: {}", hospitalInformationDto.getHospitalId(), hospitalInformationDto.getTitle());
        } catch (IllegalArgumentException e) {
            log.error("병원 정보 추가 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 정보 추가 실패 - 병원 조회 실패: {}", e.getMessage());
            throw new NoSuchElementException("해당 병원을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("병원 정보 추가 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 정보 추가에 실패했습니다.", e);
        }
    }

    /**
     * 기존 병원 정보를 업데이트합니다.
     *
     * @param hospitalId 병원 ID
     * @param title 정보 제목
     * @param information 새로운 정보 내용
     * @throws IllegalArgumentException 병원이나 정보를 찾을 수 없는 경우
     */
    @Transactional
    public void updateHospitalInformation(int hospitalId, int id, String title, String information) {
        log.debug("병원 정보 업데이트 시도 - 병원 ID: {}, 정보 ID: {}, 제목: {}", hospitalId, id, title);
        try {
            if (title == null || title.trim().isEmpty()) {
                log.error("제목이 null 이거나 비어있습니다.");
                throw new IllegalArgumentException("제목은 필수 입력값입니다.");
            }
            if (information == null || information.trim().isEmpty()) {
                log.error("정보 내용이 null 이거나 비어있습니다.");
                throw new IllegalArgumentException("정보 내용은 필수 입력값입니다.");
            }
            HospitalInformation hospitalInfo = hospitalInformationRepository.findByHospitalIdAndId(hospitalId, id)
                    .orElseThrow(() -> {
                        log.error("병원 정보를 찾을 수 없습니다 - 병원 ID: {}, 정보 ID: {}", hospitalId, id);
                        return new NoSuchElementException("해당 병원 정보를 찾을 수 없습니다.");
                    });
            hospitalInfo.setTitle(title);
            hospitalInfo.setInformation(information);
            hospitalInformationRepository.save(hospitalInfo);
            log.info("병원 정보 업데이트 성공 - 병원 ID: {}, 정보 ID: {}, 제목: {}", hospitalId, id, title);
        } catch (IllegalArgumentException e) {
            log.error("병원 정보 업데이트 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 정보 업데이트 실패 - 병원 정보 조회 실패: {}", e.getMessage());
            throw new NoSuchElementException("해당 병원 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("병원 정보 업데이트 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 정보 업데이트에 실패했습니다.", e);
        }
    }

    /**
     * 기존 병원 정보를 삭제합니다.
     *
     * @param hospitalId 병원 ID
     * @param title 정보 제목
     * @throws IllegalArgumentException 병원이나 정보를 찾을 수 없는 경우
     */
    @Transactional
    public void deleteHospitalInformation(int hospitalId, String title) {
        log.debug("병원 정보 삭제 시도 - 병원 ID: {}, 제목: {}", hospitalId, title);
        try {
            if (title == null || title.trim().isEmpty()) {
                log.error("제목이 null 이거나 비어있습니다.");
                throw new IllegalArgumentException("제목은 필수 입력값입니다.");
            }
            HospitalInformation hospitalInfo = hospitalInformationRepository.findByHospitalIdAndTitle(hospitalId, title)
                    .orElseThrow(() -> {
                        log.error("병원 정보를 찾을 수 없습니다 - 병원 ID: {}, 제목: {}", hospitalId, title);
                        return new NoSuchElementException("해당 제목의 병원 정보를 찾을 수 없습니다.");
                    });
            hospitalInformationRepository.delete(hospitalInfo);
            log.info("병원 정보 삭제 성공 - 병원 ID: {}, 제목: {}", hospitalId, title);
        } catch (IllegalArgumentException e) {
            log.error("병원 정보 삭제 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("병원 정보 삭제 실패 - 병원 정보 조회 실패: {}", e.getMessage());
            throw new NoSuchElementException("해당 병원 정보를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("병원 정보 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("병원 정보 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 두 텍스트 간의 코사인 유사도를 계산합니다.
     */
    private double calculateSimilarity(CosineSimilarity cosineSimilarity, String text1, String text2) {
        return cosineSimilarity.cosineSimilarity(
                Arrays.stream(text1.split(" "))
                        .collect(Collectors.toMap(word -> word, word -> 1)),
                Arrays.stream(text2.split(" "))
                        .collect(Collectors.toMap(word -> word, word -> 1))
        );
    }
}