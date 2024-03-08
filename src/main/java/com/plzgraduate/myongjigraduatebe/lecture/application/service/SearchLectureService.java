package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.SearchLectureCommand;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.LectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.SearchLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.SearchLectureUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class SearchLectureService implements SearchLectureUseCase {

	private final SearchLecturePort searchLecturePort;
	@Override
	public SearchLectureResponse searchLectures(SearchLectureCommand searchLectureCommand) {
		List<Lecture> lectures = searchLecturePort.searchLectureByNameOrCode(
			searchLectureCommand.getType(), searchLectureCommand.getKeyword());
		return SearchLectureResponse.from(lectures.stream().map(LectureResponse::of).collect(Collectors.toList()));
	}
}
