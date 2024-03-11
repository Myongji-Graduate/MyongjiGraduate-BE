package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.find;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindTakenLectureService implements FindTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;

	private final FindTakenLecturePort findTakenLecturePort;

	@Override
	public FindTakenLectureResponse findTakenLectures(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		List<TakenLecture> takenLectures = findTakenLecturePort.findTakenLecturesByUser(user);
		sortTakenLectures(takenLectures);
		return FindTakenLectureResponse.of(
			calculateTotalCredit(takenLectures),
			takenLectures.stream()
				.map(TakenLectureResponse::from)
				.collect(Collectors.toList())
		);
	}

	private void sortTakenLectures(List<TakenLecture> takenLectures) {
		takenLectures.sort(Comparator.comparing(TakenLecture::getYear, Collections.reverseOrder())
			.thenComparing(TakenLecture::getSemester, Comparator.nullsFirst(Comparator.reverseOrder()))
			.thenComparing(TakenLecture::getCreatedAt, Comparator.reverseOrder()));
	}

	private int calculateTotalCredit(List<TakenLecture> takenLectures) {
		int totalCredit = takenLectures
			.stream()
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();
		if(checkChapelCountIsFour(takenLectures)) {
			totalCredit += 2;
		}
		return totalCredit;
	}

	private boolean checkChapelCountIsFour(List<TakenLecture> takenLectures) {
		long chapelCount = takenLectures
			.stream()
			.filter(takenLecture -> takenLecture.getLecture().getLectureCode().equals("KMA02101"))
			.count();
		return chapelCount >= 4;
	}
}
