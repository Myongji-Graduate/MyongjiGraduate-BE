package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.SearchLectureUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchLectureService implements SearchLectureUseCase {

	private final SearchLecturePort searchLecturePort;
	private final FindTakenLectureUseCase findTakenLectureUseCase;

	@Override
	public List<SearchedLectureDto> searchLectures(Long userId, String type, String keyword) {
		List<Lecture> searchedLectures = searchLecturePort.searchLectureByNameOrCode(type, keyword);
		TakenLectureInventory takenLectureInventory = findTakenLectureUseCase.findTakenLectures(
			userId);

		List<Lecture> takenLectures = takenLectureInventory.getTakenLectures()
			.stream()
			.map(TakenLecture::getLecture)
			.collect(Collectors.toList());

		return searchedLectures.stream()
			.map(searchedLecture -> SearchedLectureDto.of(takenLectures.contains(searchedLecture),
				searchedLecture))
			.collect(Collectors.toList());
	}
}
