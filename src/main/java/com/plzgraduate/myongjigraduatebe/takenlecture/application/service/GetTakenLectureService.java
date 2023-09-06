package com.plzgraduate.myongjigraduatebe.takenlecture.application.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.GetTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.GetTakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.LoadTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class GetTakenLectureService implements GetTakenLecturePort {

	private final LoadUserPort loadUserPort;

	private final LoadTakenLecturePort loadTakenLecturePort;

	@Override
	public GetTakenLectureResponse getTakenLectures(Long userId) {
		User user = loadUserPort.loadUserById(userId);
		List<TakenLecture> takenLectures = loadTakenLecturePort.loadTakenLecturesByUser(user);
		sortTakenLectures(takenLectures);
		return GetTakenLectureResponse.from(
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
