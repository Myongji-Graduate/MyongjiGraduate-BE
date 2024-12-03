package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindLecturesService implements FindLecturesUseCase {

	private final FindLecturePort findLecturePort;

	@Override
	public List<Lecture> findLecturesByIds(List<String> lectureIds) {
		return findLecturePort.findLecturesByIds(lectureIds);
	}

	@Override
	public List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes) {
		return findLecturePort.findLecturesByLectureCodes(lectureCodes);
	}
}
