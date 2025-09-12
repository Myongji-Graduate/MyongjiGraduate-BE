package com.plzgraduate.myongjigraduatebe.timetable.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.DeleteMyTimetableRequest;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.ReplaceLecturesRequest;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.UserTimetableUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RequestMapping("/api/v1/timetables/my")
@RequiredArgsConstructor
public class UserTimetableController {

    private final UserTimetableUseCase userTimetableUseCase;

    @PostMapping("/replace")
    public ResponseEntity<Void> replaceLectures(
            @Parameter(hidden = true) @LoginUser Long userId,
            @RequestBody ReplaceLecturesRequest req
    ) {
        userTimetableUseCase.replaceLectures(userId, req.getYear(), req.getSemester(), req.getTimetableIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<TimetableResponse>> getMyLectures(
            @Parameter(hidden = true) @LoginUser Long userId,
            @RequestParam int year,
            @RequestParam int semester
    ) {
        List<TimetableResponse> result = userTimetableUseCase.getMyLectures(userId, year, semester);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteMyLectures(
            @Parameter(hidden = true) @LoginUser Long userId,
            @RequestBody DeleteMyTimetableRequest req
    ) {
        userTimetableUseCase.clearLectures(userId, req.getYear(), req.getSemester());
        return ResponseEntity.ok().build();
    }
}
