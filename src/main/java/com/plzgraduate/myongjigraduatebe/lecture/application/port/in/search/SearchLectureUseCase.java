package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search;

import java.util.List;

public interface SearchLectureUseCase {
	SearchLectureResponse searchLectures(SearchLectureCommand searchLectureCommand);
}
