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
     * @param medical_staff_id 의료진 ID
     * @param macroDto 매크로 정보
     * @throws IllegalArgumentException 매크로 이름이 중복되거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addMacro(int medical_staff_id, MacroDto macroDto) {
        if (macroDto == null) {
            log.error("매크로 정보가 null 입니다.");
            throw new IllegalArgumentException("매크로 정보는 필수입니다.");
        }
        
        if (macroDto.getMacroName() == null || macroDto.getMacroName().trim().isEmpty()) {
            log.error("매크로 이름이 비어있습니다.");
            throw new IllegalArgumentException("매크로 이름은 필수입니다.");
        }

        // 매크로 이름 중복 검사
        if (macroRepository.findByMedicalStaffIdAndMacroName(medical_staff_id, macroDto.getMacroName()).isPresent()) {
            log.error("이미 존재하는 매크로 이름입니다: {}", macroDto.getMacroName());
            throw new IllegalArgumentException("이미 존재하는 매크로 이름입니다: " + macroDto.getMacroName());
        }

        try {
            Macro macro = new Macro();
            macro.setMedicalStaffId(medical_staff_id);
            macro.setMacroId(macroDto.getMacroId());
            macro.setMacroName(macroDto.getMacroName());
            macro.setText(macroDto.getText());
            macroRepository.save(macro);
            log.info("매크로 추가 성공 - 의료진 ID: {}, 매크로 이름: {}", medical_staff_id, macroDto.getMacroName());
        } catch (Exception e) {
            log.error("매크로 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 저장에 실패했습니다.", e);
        }
    }

    /**
     * 특정 매크로의 내용을 조회합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macro_name 매크로 이름
     * @return 매크로 내용
     * @throws IllegalArgumentException 매크로를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getMacro(int medical_staff_id, String macro_name) {
        if (macro_name == null || macro_name.trim().isEmpty()) {
            log.error("매크로 이름이 비어있습니다.");
            throw new IllegalArgumentException("매크로 이름은 필수입니다.");
        }

        return macroRepository.findByMedicalStaffIdAndMacroName(medical_staff_id, macro_name)
                .orElseThrow(() -> {
                    log.error("매크로를 찾을 수 없습니다 - 의료진 ID: {}, 매크로 이름: {}", medical_staff_id, macro_name);
                    return new IllegalArgumentException("해당 매크로를 찾을 수 없습니다.");
                })
                .getText();
    }

    /**
     * 특정 의료진의 전체 매크로 목록을 조회합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 매크로 목록
     * @throws IllegalArgumentException 의료진 ID가 유효하지 않은 경우
     */
    @Transactional(readOnly = true)
    public List<MacroDto> getMacroList(Integer medical_staff_id) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            List<Macro> macroList = macroRepository.findAllByMedicalStaffId(medical_staff_id);
            List<MacroDto> macroDtoList = new ArrayList<>();
            
            for (Macro macro : macroList) {
                MacroDto macroDto = new MacroDto();
                macroDto.setMacroId(macro.getMacroId());
                macroDto.setMacroName(macro.getMacroName());
                macroDto.setMedicalStaffId(macro.getMedicalStaffId());
                macroDto.setText(macro.getText());
                macroDtoList.add(macroDto);
            }
            
            log.debug("매크로 목록 조회 성공 - 의료진 ID: {}, 매크로 수: {}", medical_staff_id, macroDtoList.size());
            return macroDtoList;
        } catch (Exception e) {
            log.error("매크로 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 목록 조회에 실패했습니다.", e);
        }
    }

    /**
     * 기존 매크로의 내용을 수정합니다.
     *
     * @param medical_staff_id 의료진의 ID
     * @param macroDto 수정할 매크로의 정보
     * @throws IllegalArgumentException 매크로를 찾을 수 없거나 필수 정보가 누락된 경우 발생
     */
    @Transactional
    public void updateMacro(Integer medical_staff_id, MacroDto macroDto) {
        if (macroDto == null) {
            log.error("매크로 정보가 유효하지 않습니다.");
            throw new IllegalArgumentException("유효한 매크로 정보가 필요합니다.");
        }
        // macroId를 사용하여 매크로를 조회합니다.
        Macro macro = macroRepository.findById(macroDto.getMacroId())
                .orElseThrow(() -> {
                    log.error("수정할 매크로를 찾을 수 없습니다 - 매크로 ID: {}", macroDto.getMacroId());
                    return new IllegalArgumentException("수정할 매크로를 찾을 수 없습니다.");
                });
        if (!macro.getMedicalStaffId().equals(medical_staff_id)) {
            log.error("의료진 ID가 일치하지 않습니다. DB의 의료진 ID: {}, 요청 의료진 ID: {}", macro.getMedicalStaffId(), medical_staff_id);
            throw new IllegalArgumentException("의료진 정보가 일치하지 않습니다.");
        }
        try {
            macro.setMacroName(macroDto.getMacroName());
            macro.setText(macroDto.getText());
            macroRepository.save(macro);
            log.info("매크로 수정 성공 - 의료진 ID: {}, 매크로 ID: {}", medical_staff_id, macroDto.getMacroId());
        } catch (Exception e) {
            log.error("매크로 수정 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 수정에 실패했습니다.", e);
        }
    }

    /**
     * 매크로를 삭제합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroName 매크로 이름
     * @throws IllegalArgumentException 매크로를 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void deleteMacro(Integer medical_staff_id, String macroName) {
        // 입력값 유효성 검사
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }
        
        if (macroName == null || macroName.trim().isEmpty()) {
            log.error("매크로 이름이 비어있습니다.");
            throw new IllegalArgumentException("매크로 이름은 필수입니다.");
        }

        try {
            // 매크로 존재 여부 확인
            Macro macro = macroRepository.findByMedicalStaffIdAndMacroName(medical_staff_id, macroName)
                    .orElseThrow(() -> {
                        log.error("삭제할 매크로를 찾을 수 없습니다 - 의료진 ID: {}, 매크로 이름: {}", medical_staff_id, macroName);
                        return new IllegalArgumentException("삭제할 매크로를 찾을 수 없습니다.");
                    });

            macroRepository.delete(macro);
            log.info("매크로 삭제 성공 - 의료진 ID: {}, 매크로 이름: {}", medical_staff_id, macroName);
        } catch (Exception e) {
            log.error("매크로 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("매크로 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 문구 머리말을 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseHead 문구 머리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addPhraseHead(Integer medical_staff_id, String phraseHead) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medical_staff_id);
                        return new IllegalArgumentException("해당 의료진을 찾을 수 없습니다.");
                    });

            medicalStaff.setPhraseHead(phraseHead);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 머리말 추가 성공 - 의료진 ID: {}", medical_staff_id);
        } catch (Exception e) {
            log.error("문구 머리말 추가 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 머리말 추가에 실패했습니다.", e);
        }
    }

    /**
     * 특정 의료진의 문구 머리말을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 문구 머리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getPhraseHead(Integer medical_staff_id) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            return medicalStaffRepository.findByMedicalStaffId(medical_staff_id)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medical_staff_id);
                        return new IllegalArgumentException("해당 의료진을 찾을 수 없습니다.");
                    })
                    .getPhraseHead();
        } catch (Exception e) {
            log.error("문구 머리말 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 머리말 조회에 실패했습니다.", e);
        }
    }

    /**
     * 문구 머리말을 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseHead 문구 머리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void updatePhraseHead(Integer medical_staff_id, String phraseHead) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medical_staff_id);
                        return new IllegalArgumentException("해당 의료진을 찾을 수 없습니다.");
                    });

            medicalStaff.setPhraseHead(phraseHead);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 머리말 수정 성공 - 의료진 ID: {}", medical_staff_id);
        } catch (Exception e) {
            log.error("문구 머리말 수정 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 머리말 수정에 실패했습니다.", e);
        }
    }

    /**
     * 문구 꼬리말을 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseTail 문구 꼬리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void addPhraseTail(Integer medical_staff_id, String phraseTail) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medical_staff_id);
                        return new IllegalArgumentException("해당 의료진을 찾을 수 없습니다.");
                    });

            medicalStaff.setPhraseTail(phraseTail);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 꼬리말 추가 성공 - 의료진 ID: {}", medical_staff_id);
        } catch (Exception e) {
            log.error("문구 꼬리말 추가 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 꼬리말 추가에 실패했습니다.", e);
        }
    }

    /**
     * 특정 의료진의 문구 꼬리말을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 문구 꼬리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public String getPhraseTail(Integer medical_staff_id) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            return medicalStaffRepository.findByMedicalStaffId(medical_staff_id)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medical_staff_id);
                        return new IllegalArgumentException("해당 의료진을 찾을 수 없습니다.");
                    })
                    .getPhraseTail();
        } catch (Exception e) {
            log.error("문구 꼬리말 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 꼬리말 조회에 실패했습니다.", e);
        }
    }

    /**
     * 문구 꼬리말을 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseTail 문구 꼬리말
     * @throws IllegalArgumentException 의료진을 찾을 수 없거나 필수 정보가 누락된 경우
     */
    @Transactional
    public void updatePhraseTail(Integer medical_staff_id, String phraseTail) {
        if (medical_staff_id == null) {
            log.error("의료진 ID가 null 입니다.");
            throw new IllegalArgumentException("의료진 ID는 필수입니다.");
        }

        try {
            MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id)
                    .orElseThrow(() -> {
                        log.error("의료진을 찾을 수 없습니다 - ID: {}", medical_staff_id);
                        return new IllegalArgumentException("해당 의료진을 찾을 수 없습니다.");
                    });

            medicalStaff.setPhraseTail(phraseTail);
            medicalStaffRepository.save(medicalStaff);
            log.info("문구 꼬리말 수정 성공 - 의료진 ID: {}", medical_staff_id);
        } catch (Exception e) {
            log.error("문구 꼬리말 수정 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("문구 꼬리말 수정에 실패했습니다.", e);
        }
    }
}