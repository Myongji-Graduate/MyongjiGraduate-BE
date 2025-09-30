package com.plzgraduate.myongjigraduatebe.timetable.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.DeleteMyTimetableRequest;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.ReplaceLecturesRequest;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.UserTimetableUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserTimetableController.class)
@Import(UserTimetableControllerTest.LoginUserResolverConfig.class)
@WithMockUser
@AutoConfigureMockMvc(addFilters = false)
class UserTimetableControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserTimetableUseCase userTimetableUseCase;

    // 테스트에서 사용할 더미 유저 ID (@LoginUser 주입값)
    private static final Long DUMMY_USER_ID = 7L;

    @Test
    @DisplayName("POST /api/v1/timetables/my/replace — 요청 본문을 받아 replaceLectures 호출하고 200 반환")
    void replaceLectures_ok() throws Exception {
        ReplaceLecturesRequest req = new ReplaceLecturesRequest();
        // 필드 세팅 (세터가 없다면 생성자/빌더에 맞춰 수정)
        setReplaceReq(req, 2025, 2, List.of(10L, 20L, 20L));

        mockMvc.perform(
                post("/api/v1/timetables/my/replace").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk());

        // 호출 파라미터 캡처 검증
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Long>> idsCaptor = ArgumentCaptor.forClass(List.class);
        verify(userTimetableUseCase).replaceLectures(eq(DUMMY_USER_ID), eq(2025), eq(2), idsCaptor.capture());

        assertThat(idsCaptor.getValue()).containsExactly(10L, 20L, 20L); // 중복 제거는 서비스에서 수행
    }

    @Test
    @DisplayName("GET /api/v1/timetables/my — year, semester 쿼리스트링을 받아 내 시간표 목록 반환")
    void getMyLectures_ok() throws Exception {
        // 응답 DTO는 필드가 프로젝트마다 다르므로, 크기만 검증
        TimetableResponse r1 = org.mockito.Mockito.mock(TimetableResponse.class);
        TimetableResponse r2 = org.mockito.Mockito.mock(TimetableResponse.class);
        when(userTimetableUseCase.getMyLectures(DUMMY_USER_ID, 2025, 1))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(
                        get("/api/v1/timetables/my")
                                .param("year", "2025")
                                .param("semester", "1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(userTimetableUseCase).getMyLectures(DUMMY_USER_ID, 2025, 1);
    }

    @Test
    @DisplayName("POST /api/v1/timetables/my/delete — 요청 본문을 받아 clearLectures 호출하고 200 반환")
    void deleteMyLectures_ok() throws Exception {
        DeleteMyTimetableRequest req = new DeleteMyTimetableRequest();
        setDeleteReq(req, 2024, 2);

        mockMvc.perform(
                post("/api/v1/timetables/my/delete").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isOk());

        verify(userTimetableUseCase).clearLectures(DUMMY_USER_ID, 2024, 2);
    }

    /** 테스트 편의를 위한 리졸버 등록 */
    @TestConfiguration
    static class LoginUserResolverConfig implements WebMvcConfigurer {
        @Bean(name = "testLoginUserArgumentResolver")
        public HandlerMethodArgumentResolver testLoginUserArgumentResolver() {
            return new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.hasParameterAnnotation(LoginUser.class)
                            && parameter.getParameterType().equals(Long.class);
                }
                @Override
                public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                              org.springframework.web.context.request.NativeWebRequest webRequest,
                                              org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                    // 항상 고정 유저 ID 반환
                    return DUMMY_USER_ID;
                }
            };
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(testLoginUserArgumentResolver());
        }
    }

    // ---- 테스트용 세터 (DTO에 빌더/세터가 없으면 프로젝트 DTO 정의에 맞춰 수정하세요) ----
    private void setReplaceReq(ReplaceLecturesRequest req, int year, int semester, List<Long> ids) throws Exception {
        // 리플렉션 기반 임시 세팅 (필드 접근 제한 시, DTO에 @Builder나 세터 추가 고려)
        var fYear = ReplaceLecturesRequest.class.getDeclaredField("year");
        var fSem = ReplaceLecturesRequest.class.getDeclaredField("semester");
        var fIds = ReplaceLecturesRequest.class.getDeclaredField("timetableIds");
        fYear.setAccessible(true);
        fSem.setAccessible(true);
        fIds.setAccessible(true);
        fYear.set(req, year);
        fSem.set(req, semester);
        fIds.set(req, ids);
    }

    private void setDeleteReq(DeleteMyTimetableRequest req, int year, int semester) throws Exception {
        var fYear = DeleteMyTimetableRequest.class.getDeclaredField("year");
        var fSem = DeleteMyTimetableRequest.class.getDeclaredField("semester");
        fYear.setAccessible(true);
        fSem.setAccessible(true);
        fYear.set(req, year);
        fSem.set(req, semester);
    }
}