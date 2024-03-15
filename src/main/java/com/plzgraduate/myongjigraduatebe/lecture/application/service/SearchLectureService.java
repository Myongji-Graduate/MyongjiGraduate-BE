package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.SearchLectureUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class SearchLectureService implements SearchLectureUseCase {

	private final SearchLecturePort searchLecturePort;

	@Override
	public List<Lecture> searchLectures(String type, String keyword) {
		return searchLecturePort.searchLectureByNameOrCode(type, keyword);
	}
}
