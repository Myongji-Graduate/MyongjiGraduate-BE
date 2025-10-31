package com.plzgraduate.myongjigraduatebe.lecturedetail.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.FindLectureInfoPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase.FindLectureInfoUseCase;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindLectureInfoService implements FindLectureInfoUseCase {

    private final FindLectureInfoPort findLectureInfoPort;

    @Override
    public List<LectureInfo> findLectureInfoBySubject(String subject) {
        return findLectureInfoPort.findBySubject(subject);
    }
}
