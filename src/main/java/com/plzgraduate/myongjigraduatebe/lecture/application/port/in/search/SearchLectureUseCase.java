package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search;

import java.util.List;

public interface SearchLectureUseCase {
	List<SearchLecture> searchLectures(SearchLectureCommand searchLectureCommand);
}
