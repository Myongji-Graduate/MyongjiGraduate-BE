package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.GetPopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.GetPopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPopularLecturesService implements GetPopularLecturesUseCase {

    private final GetPopularLecturePort getPopularLecturePort;

    @Override
    public List<FindPopularLectureDto> getPopularLecturesByTotalCount() {
        return getPopularLecturePort.getPopularLecturesByTotalCount();
    }
}
