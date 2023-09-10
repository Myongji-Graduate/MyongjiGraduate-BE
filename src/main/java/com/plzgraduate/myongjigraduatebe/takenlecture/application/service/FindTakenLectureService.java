package com.plzgraduate.myongjigraduatebe.takenlecture.application.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.FindTakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindTakenLectureService implements FindTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;

	private final com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.FindTakenLecturePort findTakenLecturePort;

	@Override
	public FindTakenLectureResponse getTakenLectures(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		List<TakenLecture> takenLectures = findTakenLecturePort.findTakenLecturesByUser(user);
		sortTakenLectures(takenLectures);
		return FindTakenLectureResponse.from(
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

}