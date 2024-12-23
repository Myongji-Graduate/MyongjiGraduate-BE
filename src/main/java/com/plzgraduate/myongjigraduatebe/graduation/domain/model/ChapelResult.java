package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChapelResult {

	public static final String CHAPEL_LECTURE_CODE = "KMA02101";
	public static final int GRADUATION_COUNT = 4;
	public static final int ANONYMOUS_TRANSFER_USER_GRADUATION_COUNT = 1;
	public static final double CHAPEL_CREDIT = 0.5;

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
			.isCompleted(false)
			.build();
	}

	private static int countTakenChapel(TakenLectureInventory takenLectureInventory) {
		return (int) takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> takenLecture.getLecture()
				.getId()
				.equals(CHAPEL_LECTURE_CODE))
			.count();
	}

	public void checkCompleted() {
		isCompleted = takenCount >= GRADUATION_COUNT;
	}

	public double getTakenChapelCredit() {
		return takenCount * CHAPEL_CREDIT;
	}

	public void checkAnonymousTransferUserChapelCount() {
		isCompleted = takenCount == ANONYMOUS_TRANSFER_USER_GRADUATION_COUNT;
	}
}
