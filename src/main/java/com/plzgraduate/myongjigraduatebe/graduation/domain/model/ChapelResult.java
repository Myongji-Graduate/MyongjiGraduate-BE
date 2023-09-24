package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChapelResult {

	public static int CHAPEL_CREDIT = 2;

	private static final String CHAPEL_LECTURE_CODE = "KMA02101";
	private static final int GRADUATION_COUNT = 4;

	private final int takenCount;
	private boolean isCompleted;

	@Builder
	public ChapelResult(int takenCount, boolean isCompleted) {
		this.takenCount = takenCount;
		this.isCompleted = isCompleted;
	}

	public static ChapelResult create(TakenLectureInventory takenLectureInventory) {
		return ChapelResult.builder()
			.takenCount(countTakenChapel(takenLectureInventory))
			.isCompleted(false).build();
	}

	public void checkCompleted() {
		isCompleted = takenCount >= GRADUATION_COUNT;
	}

	private static int countTakenChapel(TakenLectureInventory takenLectureInventory) {
		return (int)takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> takenLecture.getLecture().getLectureCode().equals(CHAPEL_LECTURE_CODE))
			.count();
	}
}
