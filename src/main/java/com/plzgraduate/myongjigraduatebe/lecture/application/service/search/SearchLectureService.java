package com.plzgraduate.myongjigraduatebe.lecture.application.service.search;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureCommand;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.LectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search.SearchLectureUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.SearchLecturePort;
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
