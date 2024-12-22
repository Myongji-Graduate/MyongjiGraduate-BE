package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationCommand;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.update.UpdateStudentInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateGraduationServiceTest {

    @InjectMocks
    private CalculateGraduationService calculateGraduationService;

    @Mock
    private FindTakenLectureUseCase findTakenLectureUseCase;

    @Mock
    private CalculateCommonCultureGraduationService calculateCommonCultureGraduationService;

    @Mock
    private CalculateCoreCultureGraduationService calculateCoreCultureGraduationService;

    @Mock
    private CalculateBasicAcademicalCultureGraduationService calculateBasicAcademicalCultureGraduationService;

    @Mock
    private CalculateMajorGraduationService calculateMajorGraduationService;

    @Mock
    private UpdateStudentInformationUseCase updateStudentInformationUseCase;

    @Mock
    private TakenLectureInventory takenLectureInventory;

    @BeforeEach
    void setup() {
        // Mock 동작 설정
        given(calculateCommonCultureGraduationService.calculateSingleDetailGraduation(any(), any(), any(), any()))
                .willReturn(DetailGraduationResult.create(null, 0, List.of()));
        given(calculateCoreCultureGraduationService.calculateSingleDetailGraduation(any(), any(), any(), any()))
                .willReturn(DetailGraduationResult.create(null, 0, List.of()));

        // updateUser 메서드 동작 설정
        doAnswer(invocation -> {
            // Mock 동작 (필요하면 로깅 추가 가능)
            UpdateStudentInformationCommand command = invocation.getArgument(0);
            System.out.println("Mocked updateUser 호출됨: " + command);
            return null; // void 반환
        }).when(updateStudentInformationUseCase).updateUser(any());
    }


    @Test
    @DisplayName("일반 학생 졸업 계산 테스트")
    void calculateGraduationForNormalStudent() {
        // Given
        User user = UserFixture.경영학과_19학번_ENG12();
        GraduationRequirement graduationRequirement = mock(GraduationRequirement.class);
        given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(takenLectureInventory);

        // When
        GraduationResult result = calculateGraduationService.calculateGraduation(user);

        // Then
        assertThat(result).isNotNull();
        verify(findTakenLectureUseCase, times(1)).findTakenLectures(user.getId());
        verify(updateStudentInformationUseCase, times(1)).updateUser(any());
    }

    @Test
    @DisplayName("편입생 졸업 계산 테스트")
    void calculateGraduationForTransferStudent() {
        // Given
        User user = UserFixture.경제학과_20학번_편입();
        GraduationRequirement graduationRequirement = mock(GraduationRequirement.class);
        given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(takenLectureInventory);

        // When
        GraduationResult result = calculateGraduationService.calculateGraduation(user);

        // Then
        assertThat(result).isNotNull();
        verify(findTakenLectureUseCase, times(1)).findTakenLectures(user.getId());
        verify(updateStudentInformationUseCase, times(1)).updateUser(any());
    }
}
