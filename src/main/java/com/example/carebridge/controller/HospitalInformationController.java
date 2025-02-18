package com.example.carebridge.controller;

import com.example.carebridge.dto.HospitalInformationDto;
import com.example.carebridge.entity.HospitalInformation;
import com.example.carebridge.service.HospitalInformationService;
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

/**
 * 병원 정보 관리 컨트롤러
 * 병원의 기본 정보 조회, 생성, 수정, 삭제 등을 처리하는 REST API 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/hospital-info")
@Tag(name = "Hospital Information Controller", description = "병원 정보 관리 API")
public class HospitalInformationController {

    private final HospitalInformationService hospitalInformationService;

    /**
     * HospitalInformationService 를 주입받는 생성자
     *
     * @param hospitalInformationService 병원 정보 서비스
     */
    public HospitalInformationController(HospitalInformationService hospitalInformationService) {
        this.hospitalInformationService = hospitalInformationService;
    }

    /**
     * 프롬프트와 가장 유사한 병원 정보를 검색합니다.
     *
     * @param prompt 검색할 프롬프트 (필수)
     * @param hospitalId 병원 ID (필수)
     * @return ResponseEntity<HospitalInformation> 검색된 병원 정보와 HTTP 상태
     */
    @Operation(summary = "유사 병원 정보 검색", description = "프롬프트와 가장 유사한 병원 정보를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 성공"),
        @ApiResponse(responseCode = "404", description = "정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/search/{hospital_id}")
    @ResponseBody
    public ResponseEntity<HospitalInformation> searchHospitalInformation(
            @Parameter(description = "검색 프롬프트", required = true)
            @RequestParam String prompt,
            @Parameter(description = "병원 ID", required = true)
            @PathVariable("hospital_id") int hospitalId) {
        try {
            log.debug("병원 정보 검색 요청 - 병원 ID: {}, 프롬프트: {}", hospitalId, prompt);
            HospitalInformation result = hospitalInformationService.findMostSimilarHospitalInformation(prompt, hospitalId);
            
            if (result == null) {
                log.info("검색 결과 없음 - 병원 ID: {}, 프롬프트: {}", hospitalId, prompt);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
            log.debug("병원 정보 검색 성공 - 병원 ID: {}", hospitalId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("병원 정보 검색 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 병원의 모든 정보를 조회합니다.
     *
     * @param hospitalId 병원 ID (필수)
     * @return ResponseEntity<List<HospitalInformationDto>> 병원 정보 목록과 HTTP 상태
     */
    @Operation(summary = "병원 정보 목록 조회", description = "특정 병원의 모든 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "204", description = "정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/list/{hospital_id}")
    @ResponseBody
    public ResponseEntity<List<HospitalInformationDto>> getHospitalInformationList(
            @Parameter(description = "병원 ID", required = true)
            @PathVariable("hospital_id") int hospitalId) {
        try {
            log.debug("병원 정보 목록 조회 요청 - 병원 ID: {}", hospitalId);
            List<HospitalInformationDto> infoList = hospitalInformationService.getHospitalInformationList(hospitalId);
            
            if (infoList.isEmpty()) {
                log.info("병원 정보가 없음 - 병원 ID: {}", hospitalId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            
            log.debug("병원 정보 목록 조회 성공 - 병원 ID: {}, 정보 수: {}", hospitalId, infoList.size());
            return new ResponseEntity<>(infoList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("병원 정보 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 특정 병원의 특정 제목을 가진 병원 정보를 조회합니다.
     *
     * @param hospital_id 병원 ID
     * @param title 정보 제목
     * @return 병원 정보 내용
     */
    @GetMapping("/{hospital_id}/{title}")
    @ResponseBody
    public ResponseEntity<String> getHospitalInformation(@PathVariable int hospital_id, @PathVariable String title) {
        try {
            return ResponseEntity.ok(hospitalInformationService.getHospitalInformation(hospital_id, title));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 새로운 병원 정보를 추가합니다.
     *
     * @param hospitalInformationDto 병원 정보 DTO (필수)
     * @return ResponseEntity<String> 처리 결과 메시지와 HTTP 상태
     */
    @Operation(summary = "병원 정보 추가", description = "새로운 병원 정보를 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "정보 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "중복된 정보 존재"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping()
    @ResponseBody
    public ResponseEntity<String> addHospitalInformation(
            @Parameter(description = "병원 정보", required = true)
            @RequestBody HospitalInformationDto hospitalInformationDto) {
        try {
            log.debug("병원 정보 추가 요청: {}", hospitalInformationDto);
            hospitalInformationService.addHospitalInformation(hospitalInformationDto);
            log.info("병원 정보 추가 성공 - 병원 ID: {}, 제목: {}", 
                    hospitalInformationDto.getHospitalId(), 
                    hospitalInformationDto.getTitle());
            return new ResponseEntity<>("병원 정보가 성공적으로 추가되었습니다.", HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 병원 정보: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("병원 정보 추가 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 병원 정보를 업데이트합니다.
     *
     * @param hospitalId 병원 ID (필수)
     * @param title 정보 제목 (필수)
     * @param information 새로운 정보 내용 (필수)
     * @return ResponseEntity<String> 처리 결과 메시지와 HTTP 상태
     */
    @Operation(summary = "병원 정보 수정", description = "기존 병원 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정보 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "수정할 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{hospital_id}/{id}")
    @ResponseBody
    public ResponseEntity<String> updateHospitalInformation(
            @Parameter(description = "병원 ID", required = true)
            @PathVariable("hospital_id") int hospitalId,
            @Parameter(description = "정보 ID", required = true)
            @PathVariable("id") int id,
            @Parameter(description = "새로운 정보 내용", required = true)
            @RequestParam String information,
            @Parameter(description = "새로운 정보 제목", required = true)
            @RequestParam String title
            ) {
        try {
            log.debug("병원 정보 수정 요청 - 병원 ID: {}, 제목: {}", hospitalId, title);
            
            if (information == null || information.trim().isEmpty()) {
                log.error("잘못된 정보 내용 - 병원 ID: {}, 제목: {}", hospitalId, title);
                return new ResponseEntity<>("정보 내용은 비워둘 수 없습니다.", HttpStatus.BAD_REQUEST);
            }
            
            hospitalInformationService.updateHospitalInformation(hospitalId, id, title, information);
            log.info("병원 정보 수정 성공 - 병원 ID: {}, 제목: {}", hospitalId, title);
            return new ResponseEntity<>("병원 정보가 성공적으로 수정되었습니다.", HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("수정할 정보를 찾을 수 없음 - 병원 ID: {}, 제목: {}", hospitalId, title, e);
            return new ResponseEntity<>("수정할 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("병원 정보 수정 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 병원 정보를 삭제합니다.
     *
     * @param hospitalId 병원 ID (필수)
     * @param title 정보 제목 (필수)
     * @return ResponseEntity<String> 처리 결과 메시지와 HTTP 상태
     */
    @Operation(summary = "병원 정보 삭제", description = "기존 병원 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정보 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제할 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{hospital_id}/{title}")
    @ResponseBody
    public ResponseEntity<String> deleteHospitalInformation(
            @Parameter(description = "병원 ID", required = true)
            @PathVariable("hospital_id") int hospitalId,
            @Parameter(description = "정보 제목", required = true)
            @PathVariable String title) {
        try {
            log.debug("병원 정보 삭제 요청 - 병원 ID: {}, 제목: {}", hospitalId, title);
            hospitalInformationService.deleteHospitalInformation(hospitalId, title);
            log.info("병원 정보 삭제 성공 - 병원 ID: {}, 제목: {}", hospitalId, title);
            return new ResponseEntity<>("병원 정보가 성공적으로 삭제되었습니다.", HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            log.error("삭제할 정보를 찾을 수 없음 - 병원 ID: {}, 제목: {}", hospitalId, title, e);
            return new ResponseEntity<>("삭제할 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("병원 정보 삭제 중 오류 발생: {}", e.getMessage(), e);
            return new ResponseEntity<>("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}