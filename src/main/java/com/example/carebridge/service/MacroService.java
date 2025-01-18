package com.example.carebridge.service;

import com.example.carebridge.dto.MacroDto;
import com.example.carebridge.entity.Macro;
import com.example.carebridge.entity.MedicalStaff;
import com.example.carebridge.repository.MacroRepository;
import com.example.carebridge.repository.MedicalStaffRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MacroService {
    private final MacroRepository macroRepository;
    private final MedicalStaffRepository medicalStaffRepository;

    public MacroService(MacroRepository macroRepository, MedicalStaffRepository medicalStaffRepository) {
        this.macroRepository = macroRepository;
        this.medicalStaffRepository = medicalStaffRepository;
    }

    /**
     * 매크로를 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroDto 매크로 정보
     */
    public void addMacro(int medical_staff_id, MacroDto macroDto) {
        Macro macro = new Macro();
        macro.setMedicalStaffId(medical_staff_id);
        macro.setMacroId(macroDto.getMacroId());
        macro.setMacroName(macroDto.getMacroName());
        macro.setText(macroDto.getText());
        macroRepository.save(macro);
    }

    /**
     * 특정 매크로의 내용을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macro_name 매크로 이름
     * @return 매크로 내용
     */
    public String getMacro(int medical_staff_id, String macro_name) {
        Macro macro = macroRepository.findByMedicalStaffIdAndMacroName(medical_staff_id, macro_name);
        return macro.getText();
    }

    /**
     * 특정 의료진의 매크로 목록을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 매크로 목록
     */
    public List<MacroDto> getMacroList(Integer medical_staff_id) {
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
        return macroDtoList;
    }

    /**
     * 매크로를 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroDto 매크로 정보
     */
    public void updateMacro(Integer medical_staff_id, MacroDto macroDto) {
        Macro macro = macroRepository.findByMedicalStaffIdAndMacroName(medical_staff_id, macroDto.getMacroName());
        macro.setMacroName(macroDto.getMacroName());
        macro.setText(macroDto.getText());
        macroRepository.save(macro);
    }

    /**
     * 매크로를 삭제합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroName 매크로 이름
     */
    public void deleteMacro(Integer medical_staff_id, String macroName) {
        Macro macro = macroRepository.findByMedicalStaffIdAndMacroName(medical_staff_id, macroName);
        macroRepository.delete(macro);
    }

    /**
     * 문구 머리말을 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseHead 문구 머리말
     */
    public void addPhraseHead(Integer medical_staff_id, String phraseHead) {
        MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id);
        medicalStaff.setPhrase_head(phraseHead);
        medicalStaffRepository.save(medicalStaff);
    }

    /**
     * 특정 의료진의 문구 머리말을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 문구 머리말
     */
    public String getPhraseHead(Integer medical_staff_id) {
        return medicalStaffRepository.findByMedicalStaffId(medical_staff_id).getPhrase_head();
    }

    /**
     * 문구 머리말을 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseHead 문구 머리말
     */
    public void updatePhraseHead(Integer medical_staff_id, String phraseHead) {
        MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id);
        medicalStaff.setPhrase_head(phraseHead);
        medicalStaffRepository.save(medicalStaff);
    }

    /**
     * 문구 꼬리말을 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseTail 문구 꼬리말
     */
    public void addPhraseTail(Integer medical_staff_id, String phraseTail) {
        MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id);
        medicalStaff.setPhrase_tail(phraseTail);
        medicalStaffRepository.save(medicalStaff);
    }

    /**
     * 특정 의료진의 문구 꼬리말을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 문구 꼬리말
     */
    public String getPhraseTail(Integer medical_staff_id) {
        return medicalStaffRepository.findByMedicalStaffId(medical_staff_id).getPhrase_tail();
    }

    /**
     * 문구 꼬리말을 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseTail 문구 꼬리말
     */
    public void updatePhraseTail(Integer medical_staff_id, String phraseTail) {
        MedicalStaff medicalStaff = medicalStaffRepository.findByMedicalStaffId(medical_staff_id);
        medicalStaff.setPhrase_tail(phraseTail);
        medicalStaffRepository.save(medicalStaff);
    }
}