package com.plzgraduate.myongjigraduatebe.timetable.api;


import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetablePageResponse;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Timetable", description = "시간표 과목 조회 API")
@WebAdapter
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final FindTimetableUseCase useCase;

    @GetMapping
    public List<TimetableResponse> getByYearAndSemester(
            @RequestParam int year,
            @RequestParam int semester
    ) {
        List<Timetable> result = useCase.findByYearAndSemester(year, semester);
        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
    }

//    @GetMapping("/search")
//    public List<TimetableResponse> search(
//            @RequestParam int year,
//            @RequestParam int semester,
//            @RequestParam String keyword
//    ) {
//        List<Timetable> result = useCase.findByKeyword(year, semester, keyword);
//        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
//    }

    @Operation(
            summary = "시간표 과목 필터링 조회 (무한스크롤)",
            description = "필터 조건에 맞는 시간표 과목을 페이지네이션으로 조회합니다. " +
                    "page와 limit 파라미터로 무한스크롤을 구현할 수 있습니다. " +
                    "응답의 nextPage가 null이면 마지막 페이지입니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TimetablePageResponse.class)
                    )
            )
    })
    @GetMapping("/search")
    public TimetablePageResponse combined(
            @Parameter(hidden = true) @LoginUser Long userId,
            @RequestParam int year,
            @RequestParam int semester,
            @RequestParam(required = false) CampusFilter campus,
            @RequestParam TakenFilter filter,
            @ModelAttribute TimetableSearchConditionRequest condition,
            @RequestParam(required = false)
            @Parameter(description = "ENUM 상수명만 허용",
                    schema = @Schema(type = "string", allowableValues = {
                            "COMMON_CULTURE", "CORE_CULTURE", "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR",
                            "DUAL_MANDATORY_MAJOR", "DUAL_ELECTIVE_MAJOR", "SUB_MAJOR", "PRIMARY_BASIC_ACADEMICAL_CULTURE",
                            "DUAL_BASIC_ACADEMICAL_CULTURE"
                    }))
            GraduationCategory recommendedCategory,
            @RequestParam(defaultValue = "1")
            @Parameter(description = "요청할 페이지 번호 (1부터 시작)", example = "1")
            int page,
            @RequestParam(defaultValue = "20")
            @Parameter(description = "한 페이지당 불러올 과목 개수", example = "20")
            int limit
    ) {
        FindTimetableUseCase.SearchCombinedResult result = useCase.searchCombinedWithPagination(
                userId, year, semester, campus, filter, condition, recommendedCategory, page, limit
        );
        
        List<TimetableResponse> data = result.getData().stream()
                .map(TimetableResponse::from)
                .collect(Collectors.toList());
        
        return TimetablePageResponse.of(data, page, limit, result.getTotalCount());
    }
}
