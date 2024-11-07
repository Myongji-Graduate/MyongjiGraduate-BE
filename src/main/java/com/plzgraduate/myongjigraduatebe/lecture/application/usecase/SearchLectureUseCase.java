package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;
import java.util.List;

public interface SearchLectureUseCase {

	List<SearchedLectureDto> searchLectures(Long userId, String type, String keyword);
}
