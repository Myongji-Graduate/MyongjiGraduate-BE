package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.update;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTakenLectureCommand {
	private Long userId;
	private List<Long> deletedTakenLectures;
	private List<Long> addedTakenLectures;

	@Builder
	private UpdateTakenLectureCommand(Long userId, List<Long> deletedTakenLectures, List<Long> addedTakenLectures) {
		this.userId = userId;
		this.deletedTakenLectures = deletedTakenLectures;
		this.addedTakenLectures = addedTakenLectures;
	}
}
