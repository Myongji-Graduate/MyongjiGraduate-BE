package com.plzgraduate.myongjigraduatebe.lecturedetail.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response.FindLectureInfoResponse;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase.FindLectureInfoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;


@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures-info")
@Validated
public class FindLectureInfoController implements FindLectureInfoApiPresentation{

    private final FindLectureInfoUseCase findLectureInfoUseCase;

    @GetMapping
    public List<FindLectureInfoResponse> searchLectureReview(
            @RequestParam String subject
    ){
        return findLectureInfoUseCase.findLectureInfoBySubject(subject)
                .stream()
                .map(FindLectureInfoResponse::from)
                .collect(Collectors.toList());
    }
}
