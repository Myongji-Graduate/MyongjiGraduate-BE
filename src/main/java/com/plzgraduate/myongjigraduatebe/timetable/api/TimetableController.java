package com.plzgraduate.myongjigraduatebe.timetable.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
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
    public List<TimetableResponse> searchWithFilter(
            @RequestParam int year,
            @RequestParam int semester,
            @ModelAttribute TimetableSearchConditionRequest condition
    ) {
        List<Timetable> result = useCase.searchByCondition(year, semester, condition);
        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
    }
}
