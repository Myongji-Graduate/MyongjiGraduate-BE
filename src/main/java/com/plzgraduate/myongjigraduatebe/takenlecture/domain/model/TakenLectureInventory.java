package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import java.util.Collections;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TakenLectureInventory {

	private final Set<TakenLecture> takenLecture;

	public Set<TakenLecture> getTakenLectures() {
		return Collections.unmodifiableSet(takenLecture);
	}

	public void handleFinishedTakenLectures(Set<TakenLecture> finishedTakenLecture) {
		takenLecture.removeAll(finishedTakenLecture);
	}
}
