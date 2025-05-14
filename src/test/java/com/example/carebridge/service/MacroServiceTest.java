package com.example.carebridge.service;

import com.example.carebridge.dto.MacroDto;
import com.example.carebridge.entity.Macro;      
import com.example.carebridge.repository.MacroRepository;
import com.example.carebridge.repository.MedicalStaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MacroService 클래스에 대한 단위 테스트
 * 매크로 관리 서비스의 주요 기능을 검증합니다.
 */
@Tag("service")
@DisplayName("매크로 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MacroServiceTest {

    @Mock
    private MacroRepository macroRepository;

    @Mock
    private MedicalStaffRepository medicalStaffRepository;

    @InjectMocks
    private MacroService macroService;

    // 공통으로 사용되는 테스트 데이터
    private Integer validMedicalStaffId;
    private String validMacroName;
    private String validMacroText;
    private Integer validMacroId;
    private Macro validMacro;
    private MacroDto validMacroDto;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        validMedicalStaffId = 1;
        validMacroName = "테스트 매크로";
        validMacroText = "이것은 테스트 매크로입니다.";
        validMacroId = 1;

        // 유효한 매크로 엔티티 생성
        validMacro = new Macro();
        validMacro.setMacroId(validMacroId);
        validMacro.setMedicalStaffId(validMedicalStaffId);
        validMacro.setMacroName(validMacroName);
        validMacro.setText(validMacroText);

        // 유효한 매크로 DTO 생성
        validMacroDto = new MacroDto();
        validMacroDto.setMacroId(validMacroId);
        validMacroDto.setMedicalStaffId(validMedicalStaffId);
        validMacroDto.setMacroName(validMacroName);
        validMacroDto.setText(validMacroText);
    }

    @Nested
    @DisplayName("addMacro 메소드 테스트")
    class AddMacroTest {

        @Test
        @DisplayName("매크로 추가 성공 테스트")
        void addMacro_Success() {
            // given
            when(macroRepository.findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName))
                    .thenReturn(Optional.empty());
            when(macroRepository.save(any(Macro.class))).thenReturn(validMacro);

            // when
            macroService.addMacro(validMedicalStaffId, validMacroDto);

            // then
            verify(macroRepository, times(1)).findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName);
            verify(macroRepository, times(1)).save(any(Macro.class));
        }

        @Test
        @DisplayName("중복된 매크로 이름으로 인한 실패 테스트")
        void addMacro_DuplicateName_Failure() {
            // given
            when(macroRepository.findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName))
                    .thenReturn(Optional.of(validMacro));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.addMacro(validMedicalStaffId, validMacroDto));
            verify(macroRepository, times(1)).findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName);
            verify(macroRepository, never()).save(any(Macro.class));
        }

        @Test
        @DisplayName("null 매크로 정보로 인한 실패 테스트")
        void addMacro_NullMacroDto_Failure() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.addMacro(validMedicalStaffId, null));
            verify(macroRepository, never()).findByMedicalStaffIdAndMacroName(any(), any());
            verify(macroRepository, never()).save(any(Macro.class));
        }

        @Test
        @DisplayName("null 매크로 이름으로 인한 실패 테스트")
        void addMacro_NullMacroName_Failure() {
            // given
            MacroDto invalidDto = new MacroDto();
            invalidDto.setMedicalStaffId(validMedicalStaffId);
            invalidDto.setText(validMacroText);
            invalidDto.setMacroName(null);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.addMacro(validMedicalStaffId, invalidDto));
            verify(macroRepository, never()).findByMedicalStaffIdAndMacroName(any(), any());
            verify(macroRepository, never()).save(any(Macro.class));
        }
    }

    @Nested
    @DisplayName("getMacro 메소드 테스트")
    class GetMacroTest {

        @Test
        @DisplayName("매크로 조회 성공 테스트")
        void getMacro_Success() {
            // given
            when(macroRepository.findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName))
                    .thenReturn(Optional.of(validMacro));

            // when
            String result = macroService.getMacro(validMedicalStaffId, validMacroName);

            // then
            assertEquals(validMacroText, result);
            verify(macroRepository, times(1)).findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName);
        }

        @Test
        @DisplayName("존재하지 않는 매크로 조회 시 예외 발생 테스트")
        void getMacro_NotFound_Failure() {
            // given
            when(macroRepository.findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> macroService.getMacro(validMedicalStaffId, validMacroName));
            verify(macroRepository, times(1)).findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName);
        }

        @Test
        @DisplayName("null 매크로 이름으로 인한 실패 테스트")
        void getMacro_NullMacroName_Failure() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.getMacro(validMedicalStaffId, null));
            verify(macroRepository, never()).findByMedicalStaffIdAndMacroName(any(), any());
        }

        @Test
        @DisplayName("빈 매크로 이름으로 인한 실패 테스트")
        void getMacro_EmptyMacroName_Failure() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.getMacro(validMedicalStaffId, ""));
            verify(macroRepository, never()).findByMedicalStaffIdAndMacroName(any(), any());
        }
    }

    @Nested
    @DisplayName("getMacroList 메소드 테스트")
    class GetMacroListTest {

        @Test
        @DisplayName("매크로 목록 조회 성공 테스트")
        void getMacroList_Success() {
            // given
            List<Macro> macroList = new ArrayList<>();
            macroList.add(validMacro);

            when(macroRepository.findAllByMedicalStaffId(validMedicalStaffId))
                    .thenReturn(Optional.of(macroList));

            // when
            List<MacroDto> result = macroService.getMacroList(validMedicalStaffId);

            // then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(validMacroId, result.get(0).getMacroId());
            assertEquals(validMacroName, result.get(0).getMacroName());
            assertEquals(validMacroText, result.get(0).getText());
            assertEquals(validMedicalStaffId, result.get(0).getMedicalStaffId());
            verify(macroRepository, times(1)).findAllByMedicalStaffId(validMedicalStaffId);
        }

        @Test
        @DisplayName("매크로가 없는 경우 예외 발생 테스트")
        void getMacroList_NoMacros_Failure() {
            // given
            when(macroRepository.findAllByMedicalStaffId(validMedicalStaffId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> macroService.getMacroList(validMedicalStaffId));
            verify(macroRepository, times(1)).findAllByMedicalStaffId(validMedicalStaffId);
        }

        @Test
        @DisplayName("null 의료진 ID로 인한 실패 테스트")
        void getMacroList_NullMedicalStaffId_Failure() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.getMacroList(null));
            verify(macroRepository, never()).findAllByMedicalStaffId(any());
        }
    }

    @Nested
    @DisplayName("updateMacro 메소드 테스트")
    class UpdateMacroTest {

        @Test
        @DisplayName("매크로 수정 성공 테스트")
        void updateMacro_Success() {
            // given
            when(macroRepository.findById(validMacroId))
                    .thenReturn(Optional.of(validMacro));
            when(macroRepository.save(any(Macro.class))).thenReturn(validMacro);

            MacroDto updateDto = new MacroDto();
            updateDto.setMacroId(validMacroId);
            updateDto.setMedicalStaffId(validMedicalStaffId);
            updateDto.setMacroName("수정된 매크로");
            updateDto.setText("이것은 수정된 매크로 내용입니다.");

            // when
            macroService.updateMacro(validMedicalStaffId, updateDto);

            // then
            ArgumentCaptor<Macro> macroCaptor = ArgumentCaptor.forClass(Macro.class);
            verify(macroRepository, times(1)).findById(validMacroId);
            verify(macroRepository, times(1)).save(macroCaptor.capture());
            
            Macro savedMacro = macroCaptor.getValue();
            assertEquals("수정된 매크로", savedMacro.getMacroName());
            assertEquals("이것은 수정된 매크로 내용입니다.", savedMacro.getText());
        }

        @Test
        @DisplayName("존재하지 않는 매크로 수정 시 예외 발생 테스트")
        void updateMacro_NotFound_Failure() {
            // given
            when(macroRepository.findById(validMacroId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> macroService.updateMacro(validMedicalStaffId, validMacroDto));
            verify(macroRepository, times(1)).findById(validMacroId);
            verify(macroRepository, never()).save(any(Macro.class));
        }

        @Test
        @DisplayName("의료진 ID 불일치로 인한 수정 실패 테스트")
        void updateMacro_MedicalStaffIdMismatch_Failure() {
            // given
            Integer differentMedicalStaffId = 2;
            when(macroRepository.findById(validMacroId))
                    .thenReturn(Optional.of(validMacro));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.updateMacro(differentMedicalStaffId, validMacroDto));
            verify(macroRepository, times(1)).findById(validMacroId);
            verify(macroRepository, never()).save(any(Macro.class));
        }

        @Test
        @DisplayName("null 매크로 정보로 인한 수정 실패 테스트")
        void updateMacro_NullMacroDto_Failure() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.updateMacro(validMedicalStaffId, null));
            verify(macroRepository, never()).findById(any());
            verify(macroRepository, never()).save(any(Macro.class));
        }
    }

    @Nested
    @DisplayName("deleteMacro 메소드 테스트")
    class DeleteMacroTest {

        @Test
        @DisplayName("매크로 삭제 성공 테스트")
        void deleteMacro_Success() {
            // given
            when(macroRepository.findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName))
                    .thenReturn(Optional.of(validMacro));
            doNothing().when(macroRepository).delete(any(Macro.class));

            // when
            macroService.deleteMacro(validMedicalStaffId, validMacroName);

            // then
            verify(macroRepository, times(1)).findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName);
            verify(macroRepository, times(1)).delete(validMacro);
        }

        @Test
        @DisplayName("존재하지 않는 매크로 삭제 시 예외 발생 테스트")
        void deleteMacro_NotFound_Failure() {
            // given
            when(macroRepository.findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName))
                    .thenReturn(Optional.empty());

            // when & then
            assertThrows(NoSuchElementException.class, () -> macroService.deleteMacro(validMedicalStaffId, validMacroName));
            verify(macroRepository, times(1)).findByMedicalStaffIdAndMacroName(validMedicalStaffId, validMacroName);
            verify(macroRepository, never()).delete(any(Macro.class));
        }

        @Test
        @DisplayName("null 매크로 이름으로 인한 삭제 실패 테스트")
        void deleteMacro_NullMacroName_Failure() {
            // when & then
            assertThrows(IllegalArgumentException.class, () -> macroService.deleteMacro(validMedicalStaffId, null));
            verify(macroRepository, never()).findByMedicalStaffIdAndMacroName(any(), any());
            verify(macroRepository, never()).delete(any(Macro.class));
        }
    }
} 