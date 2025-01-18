package com.example.carebridge.controller;

import com.example.carebridge.dto.MacroDto;
import com.example.carebridge.service.MacroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/macro")
public class MacroController {
    private final MacroService macroService;

    public MacroController(MacroService macroService) {
        this.macroService = macroService;
    }

    /**
     * 매크로를 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroDto 매크로 정보
     * @return 성공 메시지 또는 오류 메시지
     */
    @PostMapping("{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> addMacro(@PathVariable Integer medical_staff_id, @RequestBody MacroDto macroDto) {
        try {
            macroService.addMacro(medical_staff_id, macroDto);
            return ResponseEntity.ok("Macro added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding macro: " + e.getMessage());
        }
    }

    /**
     * 특정 매크로를 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macro_name 매크로 이름
     * @return 매크로 내용 또는 오류 메시지
     */
    @GetMapping("{medical_staff_id}/{macro_name}")
    @ResponseBody
    public ResponseEntity<String> getMacro(@PathVariable Integer medical_staff_id, @PathVariable String macro_name) {
        try {
            return ResponseEntity.ok(macroService.getMacro(medical_staff_id, macro_name));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 특정 의료진의 매크로 목록을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 매크로 목록 또는 오류 메시지
     */
    @GetMapping("list/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<List<MacroDto>> getMacro(@PathVariable Integer medical_staff_id) {
        try {
            List<MacroDto> macroList = macroService.getMacroList(medical_staff_id);
            return ResponseEntity.ok(macroList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 매크로를 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroDto 매크로 정보
     * @return 성공 메시지 또는 오류 메시지
     */
    @PutMapping("{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> updateMacro(@PathVariable Integer medical_staff_id, @RequestBody MacroDto macroDto) {
        try {
            macroService.updateMacro(medical_staff_id, macroDto);
            return ResponseEntity.ok("Macro updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating macro: " + e.getMessage());
        }
    }

    /**
     * 매크로를 삭제합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param macroName 매크로 이름
     * @return 성공 메시지 또는 오류 메시지
     */
    @DeleteMapping("{medical_staff_id}/{macroName}")
    @ResponseBody
    public ResponseEntity<String> deleteMacro(@PathVariable Integer medical_staff_id, @PathVariable String macroName) {
        try {
            macroService.deleteMacro(medical_staff_id, macroName);
            return ResponseEntity.ok("Macro deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting macro: " + e.getMessage());
        }
    }

    /**
     * 문구 머리말을 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseHead 문구 머리말
     * @return 성공 메시지 또는 오류 메시지
     */
    @PostMapping("phrase-head/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> addPhraseHead(@PathVariable Integer medical_staff_id, @RequestParam String phraseHead) {
        try {
            macroService.addPhraseHead(medical_staff_id, phraseHead);
            return ResponseEntity.ok("Phrase head added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding phrase head: " + e.getMessage());
        }
    }

    /**
     * 문구 머리말을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 문구 머리말 또는 오류 메시지
     */
    @GetMapping("phrase-head/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> getPhraseHead(@PathVariable Integer medical_staff_id) {
        try {
            String phraseHead = macroService.getPhraseHead(medical_staff_id);
            return ResponseEntity.ok(phraseHead);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 문구 머리말을 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseHead 문구 머리말
     * @return 성공 메시지 또는 오류 메시지
     */
    @PutMapping("phrase-head/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> updatePhraseHead(@PathVariable Integer medical_staff_id, @RequestParam String phraseHead) {
        try {
            macroService.updatePhraseHead(medical_staff_id, phraseHead);
            return ResponseEntity.ok("Phrase head updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating phrase head: " + e.getMessage());
        }
    }

    /**
     * 문구 꼬리말을 추가합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseTail 문구 꼬리말
     * @return 성공 메시지 또는 오류 메시지
     */
    @PostMapping("phrase-tail/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> addPhraseTail(@PathVariable Integer medical_staff_id, @RequestParam String phraseTail) {
        try {
            macroService.addPhraseTail(medical_staff_id, phraseTail);
            return ResponseEntity.ok("Phrase tail added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding phrase tail: " + e.getMessage());
        }
    }

    /**
     * 문구 꼬리말을 반환합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @return 문구 꼬리말 또는 오류 메시지
     */
    @GetMapping("phrase-tail/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> getPhraseTail(@PathVariable Integer medical_staff_id) {
        try {
            String phraseTail = macroService.getPhraseTail(medical_staff_id);
            return ResponseEntity.ok(phraseTail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 문구 꼬리말을 업데이트합니다.
     *
     * @param medical_staff_id 의료진 ID
     * @param phraseTail 문구 꼬리말
     * @return 성공 메시지 또는 오류 메시지
     */
    @PutMapping("phrase-tail/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> updatePhraseTail(@PathVariable Integer medical_staff_id, @RequestParam String phraseTail) {
        try {
            macroService.updatePhraseTail(medical_staff_id, phraseTail);
            return ResponseEntity.ok("Phrase tail updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating phrase tail: " + e.getMessage());
        }
    }
}