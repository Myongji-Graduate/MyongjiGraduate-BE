package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;

public class TakenLectureInventory {

	private final Set<TakenLecture> takenLecture;

	@Builder
	private TakenLectureInventory(Set<TakenLecture> takenLecture) {
		this.takenLecture = takenLecture;
	}

	public Set<TakenLecture> getTakenLectures() {
		return Collections.unmodifiableSet(takenLecture);
	}

	public Set<TakenLecture> getCultureLectures() {
		return takenLecture.stream()
			.filter(taken -> taken.getLecture().isCulture())
			.collect(Collectors.toSet());
	}

	public void handleFinishedTakenLectures(Set<TakenLecture> finishedTakenLecture) {
		takenLecture.removeAll(finishedTakenLecture);
	}

	public static TakenLectureInventory from(Set<TakenLecture> takenLectures) {
		return TakenLectureInventory.builder()
			.takenLecture(takenLectures)
			.build();
	}
}
