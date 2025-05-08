package com.example.carebridge.controller;

import com.example.carebridge.dto.MacroDto;
import com.example.carebridge.service.MacroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 매크로 관리 컨트롤러
 * 의료진의 매크로, 문구 머리말, 꼬리말 등을 관리하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/macro")
@Tag(name = "Macro Controller", description = "매크로 관리 API")
public class MacroController {
    private final MacroService macroService;

    public MacroController(MacroService macroService) {
        this.macroService = macroService;
    }

    /**
     * 새로운 매크로를 추가하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param macroDto 매크로 정보를 담은 DTO
     * @return 매크로 추가 결과 메시지와 상태 코드
     */
    @Operation(summary = "매크로 추가", description = "의료진의 새로운 매크로를 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "매크로 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "중복된 매크로 이름"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> addMacro(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "매크로 정보", required = true)
            @RequestBody MacroDto macroDto) {
        try {
            log.debug("매크로 추가 요청 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroDto.getMacroName());
            macroService.addMacro(medicalStaffId, macroDto);
            log.info("매크로 추가 성공 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroDto.getMacroName());
            return new ResponseEntity<>("매크로가 성공적으로 추가되었습니다.", HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 매크로를 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 의료진의 매크로를 찾을 수 없습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 매크로 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("매크로 추가 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 매크로의 내용을 조회하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param macroName 조회할 매크로 이름
     * @return 매크로 내용과 상태 코드
     */
    @Operation(summary = "매크로 조회", description = "특정 매크로의 내용을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "매크로 조회 성공"),
        @ApiResponse(responseCode = "404", description = "매크로를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("{medical_staff_id}/{macro_name}")
    @ResponseBody
    public ResponseEntity<String> getMacro(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "매크로 이름", required = true)
            @PathVariable("macro_name") String macroName) {
        try {
            log.debug("매크로 조회 요청 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
            String macro = macroService.getMacro(medicalStaffId, macroName);

            log.debug("매크로 조회 성공 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
            return new ResponseEntity<>(macro, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 매크로를 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("해당 ID의 의료진의 매크로를 찾을 수 없습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 매크로 조회 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("매크로 조회 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 의료진의 모든 매크로 목록을 조회하는 메소드
     * @param medicalStaffId 의료진 ID
     * @return 매크로 목록과 상태 코드
     */
    @Operation(summary = "매크로 목록 조회", description = "의료진의 모든 매크로 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "매크로 목록 조회 성공"),
        @ApiResponse(responseCode = "204", description = "매크로가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("list/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<List<MacroDto>> getMacroList(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId) {
        try {
            log.debug("매크로 목록 조회 요청 - 의료진 ID: {}", medicalStaffId);
            List<MacroDto> macroList = macroService.getMacroList(medicalStaffId);

            log.debug("매크로 목록 조회 성공 - 의료진 ID: {}, 매크로 수: {}", medicalStaffId, macroList.size());
            return new ResponseEntity<>(macroList, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 매크로를 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            log.error("매크로 목록 조회 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * 기존 매크로의 내용을 수정하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param macroDto 수정할 매크로 정보를 담은 DTO
     * @return 매크로 수정 결과 메시지와 상태 코드
     */
    @Operation(summary = "매크로 수정", description = "기존 매크로의 내용을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "매크로 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "매크로를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> updateMacro(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "매크로 정보", required = true)
            @RequestBody MacroDto macroDto) {
        try {
            log.debug("매크로 수정 요청 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroDto.getMacroName());
            macroService.updateMacro(medicalStaffId, macroDto);
            log.info("매크로 수정 성공 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroDto.getMacroName());
            return new ResponseEntity<>("매크로가 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 매크로를 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 매크로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 : {}, 의료진 ID: {}", e.getMessage(), medicalStaffId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("매크로 수정 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 기존 매크로를 삭제하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param macroName 삭제할 매크로 이름
     * @return 매크로 삭제 결과 메시지와 상태 코드
     */
    @Operation(summary = "매크로 삭제", description = "기존 매크로를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "매크로 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "매크로를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("{medical_staff_id}/{macroName}")
    @ResponseBody
    public ResponseEntity<String> deleteMacro(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "매크로 이름", required = true)
            @PathVariable String macroName) {
        try {
            log.debug("매크로 삭제 요청 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
            macroService.deleteMacro(medicalStaffId, macroName);
            log.info("매크로 삭제 성공 - 의료진 ID: {}, 매크로 이름: {}", medicalStaffId, macroName);
            return new ResponseEntity<>("매크로가 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 매크로를 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 매크로를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 : {}, 의료진 ID: {}, 매크로 이름: {}", e.getMessage(), medicalStaffId, macroName, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("매크로 삭제 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 의료진의 문구 머리말을 추가하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param phraseHead 추가할 문구 머리말
     * @return 문구 머리말 추가 결과 메시지와 상태 코드
     */
    @Operation(summary = "문구 머리말 추가", description = "의료진의 문구 머리말을 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "문구 머리말 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("phrase-head/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> addPhraseHead(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "문구 머리말", required = true)
            @RequestParam String phraseHead) {
        try {
            log.debug("문구 머리말 추가 요청 - 의료진 ID: {}", medicalStaffId);
            macroService.addPhraseHead(medicalStaffId, phraseHead);
            log.info("문구 머리말 추가 성공 - 의료진 ID: {}", medicalStaffId);
            return new ResponseEntity<>("문구 머리말이 성공적으로 추가되었습니다.", HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 머리말을 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 머리말을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("문구 머리말 추가 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 의료진의 문구 머리말을 조회하는 메소드
     * @param medicalStaffId 의료진 ID
     * @return 문구 머리말과 상태 코드
     */
    @Operation(summary = "문구 머리말 조회", description = "의료진의 문구 머리말을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "문구 머리말 조회 성공"),
        @ApiResponse(responseCode = "404", description = "문구 머리말을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("phrase-head/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> getPhraseHead(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId) {
        try {
            log.debug("문구 머리말 조회 요청 - 의료진 ID: {}", medicalStaffId);
            String phraseHead = macroService.getPhraseHead(medicalStaffId);

            log.debug("문구 머리말 조회 성공 - 의료진 ID: {}", medicalStaffId);
            return new ResponseEntity<>(phraseHead, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 머리말을 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 머리말을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("문구 머리말 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 의료진의 문구 머리말을 수정하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param phraseHead 새로운 문구 머리말
     * @return 문구 머리말 수정 결과 메시지와 상태 코드
     */
    @Operation(summary = "문구 머리말 수정", description = "의료진의 문구 머리말을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "문구 머리말 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "문구 머리말을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("phrase-head/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> updatePhraseHead(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "새로운 문구 머리말", required = true)
            @RequestParam String phraseHead) {
        try {
            log.debug("문구 머리말 수정 요청 - 의료진 ID: {}", medicalStaffId);
            macroService.updatePhraseHead(medicalStaffId, phraseHead);
            log.info("문구 머리말 수정 성공 - 의료진 ID: {}", medicalStaffId);
            return new ResponseEntity<>("문구 머리말이 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 머리말을 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 머리말을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("문구 머리말 수정 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 의료진의 문구 꼬리말을 추가하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param phraseTail 추가할 문구 꼬리말
     * @return 문구 꼬리말 추가 결과 메시지와 상태 코드
     */
    @Operation(summary = "문구 꼬리말 추가", description = "의료진의 문구 꼬리말을 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "문구 꼬리말 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("phrase-tail/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> addPhraseTail(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "문구 꼬리말", required = true)
            @RequestParam String phraseTail) {
        try {
            log.debug("문구 꼬리말 추가 요청 - 의료진 ID: {}", medicalStaffId);

            macroService.addPhraseTail(medicalStaffId, phraseTail);
            log.info("문구 꼬리말 추가 성공 - 의료진 ID: {}", medicalStaffId);
            return new ResponseEntity<>("문구 꼬리말이 성공적으로 추가되었습니다.", HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 꼬리말을 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 꼬리말을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("문구 꼬리말 추가 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 의료진의 문구 꼬리말을 조회하는 메소드
     * @param medicalStaffId 의료진 ID
     * @return 문구 꼬리말과 상태 코드
     */
    @Operation(summary = "문구 꼬리말 조회", description = "의료진의 문구 꼬리말을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "문구 꼬리말 조회 성공"),
        @ApiResponse(responseCode = "404", description = "문구 꼬리말을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("phrase-tail/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> getPhraseTail(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId) {
        try {
            log.debug("문구 꼬리말 조회 요청 - 의료진 ID: {}", medicalStaffId);
            String phraseTail = macroService.getPhraseTail(medicalStaffId);

            log.debug("문구 꼬리말 조회 성공 - 의료진 ID: {}", medicalStaffId);
            return new ResponseEntity<>(phraseTail, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 꼬리말을 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 꼬리말을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("문구 꼬리말 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 의료진의 문구 꼬리말을 수정하는 메소드
     * @param medicalStaffId 의료진 ID
     * @param phraseTail 새로운 문구 꼬리말
     * @return 문구 꼬리말 수정 결과 메시지와 상태 코드
     */
    @Operation(summary = "문구 꼬리말 수정", description = "의료진의 문구 꼬리말을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "문구 꼬리말 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "문구 꼬리말을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("phrase-tail/{medical_staff_id}")
    @ResponseBody
    public ResponseEntity<String> updatePhraseTail(
            @Parameter(description = "의료진 ID", required = true)
            @PathVariable("medical_staff_id") Integer medicalStaffId,
            @Parameter(description = "새로운 문구 꼬리말", required = true)
            @RequestParam String phraseTail) {
        try {
            log.debug("문구 꼬리말 수정 요청 - 의료진 ID: {}", medicalStaffId);
            macroService.updatePhraseTail(medicalStaffId, phraseTail);

            log.info("문구 꼬리말 수정 성공 - 의료진 ID: {}", medicalStaffId);
            return new ResponseEntity<>("문구 꼬리말이 성공적으로 수정되었습니다.", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.warn("해당 ID의 의료진의 꼬리말을 찾을 수 없습니다 - 의료진 ID: {}", medicalStaffId, e);
            return new ResponseEntity<>("해당 의료진의 꼬리말을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 요청 - 의료진 ID: {}, 오류: {}", medicalStaffId, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("문구 꼬리말 수정 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}