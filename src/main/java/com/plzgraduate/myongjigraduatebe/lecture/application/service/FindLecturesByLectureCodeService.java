package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.find.FindLecturesByLectureCodeUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindLecturesByLectureCodeService implements FindLecturesByLectureCodeUseCase {

	private final FindLecturePort findLecturePort;
	@Override
	public List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes) {
		return findLecturePort.findLecturesByLectureCodes(lectureCodes);
	}
}
