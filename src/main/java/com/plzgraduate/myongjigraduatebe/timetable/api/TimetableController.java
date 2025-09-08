package com.plzgraduate.myongjigraduatebe.timetable.api;

//import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
//import com.plzgraduate.myongjigraduatebe.timetable.application.service.FindTimeTableService;
//import com.plzgraduate.myongjigraduatebe.timetable.application.service.FindTimeTableService.FilterType;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
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

//    @GetMapping("/filter")
//    public List<TimetableResponse> searchWithFilter(
//            @RequestParam int year,
//            @RequestParam int semester,
//            @ModelAttribute TimetableSearchConditionRequest condition
//    ) {
//        List<Timetable> result = useCase.searchByCondition(year, semester, condition);
//        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
//    }

//    @GetMapping("/taken")
//    public List<TimetableResponse> getTaken(
//            @LoginUser Long userId,
//            @RequestParam int year,
//            @RequestParam int semester
//    ) {
//        List<Timetable> result = ((FindTimeTableService) useCase).filterByTakenStatus(userId, year, semester, FilterType.TAKEN);
//        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
//    }
//
//    @GetMapping("/not-taken")
//    public List<TimetableResponse> getNotTaken(
//            @LoginUser Long userId,
//            @RequestParam int year,
//            @RequestParam int semester
//    ) {
//        List<Timetable> result = ((FindTimeTableService) useCase).filterByTakenStatus(userId, year, semester, FilterType.NOT_TAKEN);
//        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
//    }

    @GetMapping("/search/combined")
    public List<TimetableResponse> searchCombined(
            @RequestParam int year,
            @RequestParam int semester,
            @RequestParam TakenFilter filter,  // ALL/TAKEN/NOT_TAKEN
            @LoginUser Long userId,        // TAKEN/NOT_TAKEN일 때 필요
            @ModelAttribute TimetableSearchConditionRequest condition
    ) {
        // “미이수 시 자연교양/인문교양/사용자전공만” 조건을 적용하고 싶으면 플래그 전달
        boolean restrictToMajorAndCommons = true;
        List<Timetable> result = useCase.searchCombined(
                userId, year, semester, filter, condition, restrictToMajorAndCommons);
        return result.stream().map(TimetableResponse::from).collect(Collectors.toList());
    }

}
