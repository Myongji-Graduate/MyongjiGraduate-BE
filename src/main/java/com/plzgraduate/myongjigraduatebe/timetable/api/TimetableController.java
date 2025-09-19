package com.plzgraduate.myongjigraduatebe.timetable.api;


import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/search")
    public List<TimetableResponse> search(
            @RequestParam int year,
            @RequestParam int semester,
            @RequestParam String keyword
    ) {
        List<Timetable> result = useCase.findByKeyword(year, semester, keyword);
        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
    }

    @GetMapping("/filter")
    public List<TimetableResponse> combined(
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
            GraduationCategory recommendedCategory
    ) {
        List<Timetable> result = useCase.searchCombined(
                userId, year, semester, campus, filter, condition, recommendedCategory
        );
        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
    }
}
