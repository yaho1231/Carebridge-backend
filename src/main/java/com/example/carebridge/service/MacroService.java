package com.example.carebridge.service;

import com.example.carebridge.dto.MacroDto;
import com.example.carebridge.entity.Macro;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.repository.MacroRepository;
import com.example.carebridge.repository.MedicalStaffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 매크로 관리 서비스
 * 의료진의 매크로, 문구 머리말, 꼬리말 등을 관리하는 서비스 클래스입니다.
 */
@Slf4j
@Service
public class MacroService {
    private final MacroRepository macroRepository;
    private final MedicalStaffRepository medicalStaffRepository;

    /**
     * MacroRepository 와 MedicalStaffRepository 를 주입받는 생성자
     * 
     * @param macroRepository 매크로 저장소
     * @param medicalStaffRepository 의료진 저장소
     */
    public MacroService(MacroRepository macroRepository, MedicalStaffRepository medicalStaffRepository) {
        this.macroRepository = macroRepository;
        this.medicalStaffRepository = medicalStaffRepository;
    }

    /**
     * 새로운 매크로를 추가합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param macroDto 매크로 정보
     * @throws IllegalArgumentException 매크로 이름이 중복되거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addMacro(int medicalStaffId, MacroDto macroDto) {
        log.debug("매크로 추가 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (macroDto == null) {
                log.error("매크로 정보가 null 입니다.");
                throw new IllegalArgumentException("매크로 정보는 필수입니다.");
            }
            if (macroDto.getMacroName() == null || macroDto.getMacroName().trim().isEmpty()) {
                log.error("매크로 이름이 비어있습니다.");
                throw new IllegalArgumentException("매크로 이름은 필수입니다.");
            }
            if (macroRepository.findByMedicalStaffIdAndMacroName(medicalStaffId, macroDto.getMacroName()).isPresent()) {
                log.error("이미 존재하는 매크로 이름입니다 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroDto.getMacroName());
                throw new IllegalArgumentException("이미 존재하는 매크로 이름입니다: " + macroDto.getMacroName());
            }
            Macro macro = new Macro();
            macro.setMedicalStaffId(medicalStaffId);
            macro.setMacroId(macroDto.getMacroId());
            macro.setMacroName(macroDto.getMacroName());
            macro.setText(macroDto.getText());
            macroRepository.save(macro);
            log.info("매크로 추가 성공 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroDto.getMacroName());
        } catch (IllegalArgumentException e) {
            log.error("매크로 추가 실패 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("매크로 추가 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 추가에 실패했습니다.", e);
        }
    }

    /**
     * 특정 매크로의 내용을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param macroName 매크로 이름
     * @return 매크로 내용
     * @throws IllegalArgumentException 매크로를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getMacro(int medicalStaffId, String macroName) {
        log.debug("매크로 조회 시도 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
        try {
            if (macroName == null || macroName.trim().isEmpty()) {
                log.error("매크로 이름이 비어있습니다.");
                throw new IllegalArgumentException("매크로 이름은 필수입니다.");
            }
            return macroRepository.findByMedicalStaffIdAndMacroName(medicalStaffId, macroName)
                    .map(Macro::getText)
                    .orElseThrow(() -> {
                        log.error("매크로를 찾을 수 없습니다 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
                        return new NoSuchElementException("해당 매크로를 찾을 수 없습니다: " + macroName);
                    });
        }  catch (IllegalArgumentException e) {
            log.error("매크로 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("매크로 조회 실패 혹은 의료진 조회 실패: {}", e.getMessage());
            throw new NoSuchElementException("해당 매크로를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("매크로 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 조회에 실패했습니다.", e);
        }
    }

    /**
     * 특정 의료진의 전체 매크로 목록을 조회합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 매크로 목록
     * @throws IllegalArgumentException 의료진 ID가 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public List<MacroDto> getMacroList(Integer medicalStaffId) {
        log.debug("매크로 목록 조회 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            List<Macro> macroList = macroRepository.findAllByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("매크로를 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진의 매크로를 찾을 수 없습니다.");
                    });
            List<MacroDto> macroDtoList = new ArrayList<>();
            for (Macro macro : macroList) {
                MacroDto macroDto = new MacroDto();
                macroDto.setMacroId(macro.getMacroId());
                macroDto.setMacroName(macro.getMacroName());
                macroDto.setMedicalStaffId(macro.getMedicalStaffId());
                macroDto.setText(macro.getText());
                macroDtoList.add(macroDto);
            }
            log.debug("매크로 목록 조회 성공 - 의료진 ID: {}, 매크로 수: {}", medicalStaffId, macroDtoList.size());
            return macroDtoList;
        } catch (IllegalArgumentException e) {
            log.error("매크로 목록 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("매크로 목록 조회 실패 - 매크로 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진의 매크로를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("매크로 목록 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 목록 조회에 실패했습니다.", e);
        }
    }

    /**
     * 기존 매크로의 내용을 수정합니다.
     *
     * @param medicalStaffId 의료진의 ID
     * @param macroDto 수정할 매크로의 정보
     * @throws IllegalArgumentException 매크로를 찾을 수 없거나 필수 정보가 누락된 경우 발생
     */
    @Transactional
    public void updateMacro(Integer medicalStaffId, MacroDto macroDto) {
        log.debug("매크로 수정 시도 - 의료진 ID: {}, 매크로 ID: {}", medicalStaffId, (macroDto != null ? macroDto.getMacroId() : "null"));
        try {
            if (medicalStaffId == null || macroDto == null) {
                log.error("의료진 ID 또는 매크로 정보가 null입니다.");
                throw new IllegalArgumentException("의료진 ID와 유효한 매크로 정보가 필요합니다.");
            }
            Macro macro = macroRepository.findById(macroDto.getMacroId())
                    .orElseThrow(() -> {
                        log.error("수정할 매크로를 찾을 수 없습니다 - 매크로 ID: {}", macroDto.getMacroId());
                        return new NoSuchElementException("수정할 매크로를 찾을 수 없습니다.");
                    });
            if (!macro.getMedicalStaffId().equals(medicalStaffId)) {
                log.error("의료진 ID가 일치하지 않습니다. DB의 의료진 ID: {}, 요청 의료진 ID: {}", macro.getMedicalStaffId(), medicalStaffId);
                throw new IllegalArgumentException("의료진 정보가 일치하지 않습니다.");
            }
            macro.setMacroName(macroDto.getMacroName());
            macro.setText(macroDto.getText());
            macroRepository.save(macro);
            log.info("매크로 수정 성공 - 의료진 ID: {}, 매크로 ID: {}", medicalStaffId, macroDto.getMacroId());
        } catch (IllegalArgumentException e) {
            log.error("매크로 수정 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("매크로 수정 실패 - 매크로 없음: {}", e.getMessage());
            throw new NoSuchElementException("수정할 매크로를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("매크로 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 수정에 실패했습니다.", e);
        }
    }

    /**
     * 매크로를 삭제합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param macroName 매크로 이름
     * @throws IllegalArgumentException 매크로를 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void deleteMacro(Integer medicalStaffId, String macroName) {
        log.debug("매크로 삭제 시도 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            if (macroName == null || macroName.trim().isEmpty()) {
                log.error("매크로 이름이 비어있습니다.");
                throw new IllegalArgumentException("매크로 이름은 필수입니다.");
            }
            Macro macro = macroRepository.findByMedicalStaffIdAndMacroName(medicalStaffId, macroName)
                    .orElseThrow(() -> {
                        log.error("삭제할 매크로를 찾을 수 없습니다 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
                        return new NoSuchElementException("삭제할 매크로를 찾을 수 없습니다.");
                    });
            macroRepository.delete(macro);
            log.info("매크로 삭제 성공 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
        } catch (IllegalArgumentException e) {
            log.error("매크로 삭제 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("매크로 삭제 실패 - 매크로 없음: {}", e.getMessage());
            throw new NoSuchElementException("삭제할 매크로를 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("매크로 삭제 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 문구 머리말을 추가합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param phraseHead 문구 머리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addPhraseHead(Integer medicalStaffId, String phraseHead) {
        log.debug("문구 머리말 추가 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
                    });
            medicalStaff.setPhraseHead(phraseHead);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 머리말 추가 성공 - 의료진 ID: {}", medicalStaffId);
        } catch (IllegalArgumentException e) {
            log.error("문구 머리말 추가 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("문구 머리말 추가 실패 - 의료진 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("문구 머리말 추가 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 머리말 추가에 실패했습니다.", e);
        }
    }

    /**
     * 특정 의료진의 문구 머리말을 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 문구 머리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getPhraseHead(Integer medicalStaffId) {
        log.debug("문구 머리말 조회 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            return medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
                    })
                    .getPhraseHead();
        } catch (IllegalArgumentException e) {
            log.error("문구 머리말 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("문구 머리말 조회 실패 - 의료진 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("문구 머리말 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 머리말 조회에 실패했습니다.", e);
        }
    }

    /**
     * 문구 머리말을 업데이트합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param phraseHead 문구 머리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void updatePhraseHead(Integer medicalStaffId, String phraseHead) {
        log.debug("문구 머리말 수정 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
                    });
            medicalStaff.setPhraseHead(phraseHead);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 머리말 수정 성공 - 의료진 ID: {}", medicalStaffId);
        } catch (IllegalArgumentException e) {
            log.error("문구 머리말 수정 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("문구 머리말 수정 실패 - 의료진 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("문구 머리말 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 머리말 수정에 실패했습니다.", e);
        }
    }

    /**
     * 문구 꼬리말을 추가합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param phraseTail 문구 꼬리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addPhraseTail(Integer medicalStaffId, String phraseTail) {
        log.debug("문구 꼬리말 추가 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
                    });
            medicalStaff.setPhraseTail(phraseTail);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 꼬리말 추가 성공 - 의료진 ID: {}", medicalStaffId);
        } catch (IllegalArgumentException e) {
            log.error("문구 꼬리말 추가 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("문구 꼬리말 추가 실패 - 의료진 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("문구 꼬리말 추가 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 꼬리말 추가에 실패했습니다.", e);
        }
    }

    /**
     * 특정 의료진의 문구 꼬리말을 반환합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @return 문구 꼬리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getPhraseTail(Integer medicalStaffId) {
        log.debug("문구 꼬리말 조회 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            return medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
                    })
                    .getPhraseTail();
        } catch (IllegalArgumentException e) {
            log.error("문구 꼬리말 조회 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("문구 꼬리말 조회 실패 - 의료진 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("문구 꼬리말 조회 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 꼬리말 조회에 실패했습니다.", e);
        }
    }

    /**
     * 문구 꼬리말을 업데이트합니다.
     *
     * @param medicalStaffId 의료진 ID
     * @param phraseTail 문구 꼬리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void updatePhraseTail(Integer medicalStaffId, String phraseTail) {
        log.debug("문구 꼬리말 수정 시도 - 의료진 ID: {}", medicalStaffId);
        try {
            if (medicalStaffId == null) {
                log.error("의료진 ID가 null입니다.");
                throw new IllegalArgumentException("의료진 ID는 필수입니다.");
            }
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medicalStaffId)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medicalStaffId);
                        return new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
                    });
            medicalStaff.setPhraseTail(phraseTail);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 꼬리말 수정 성공 - 의료진 ID: {}", medicalStaffId);
        } catch (IllegalArgumentException e) {
            log.error("문구 꼬리말 수정 실패 - 입력값 오류: {}", e.getMessage());
            throw new IllegalArgumentException("잘못된 입력값입니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("문구 꼬리말 수정 실패 - 의료진 없음: {}", e.getMessage());
            throw new NoSuchElementException("해당 의료진을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error("문구 꼬리말 수정 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 꼬리말 수정에 실패했습니다.", e);
        }
    }
}