package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.update;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.update.UpdateTakenLectureCommand;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTakenLectureRequest {

	private List<Long> deletedTakenLectures;

	private List<Long> addedTakenLectures;

	public UpdateTakenLectureRequest(List<Long> deletedTakenLectures, List<Long> addedTakenLectures) {
		this.deletedTakenLectures = deletedTakenLectures;
		this.addedTakenLectures = addedTakenLectures;
	}

	public UpdateTakenLectureCommand toCommand(Long userId) {
		return UpdateTakenLectureCommand.builder()
			.userId(userId)
			.deletedTakenLectures(deletedTakenLectures)
			.addedTakenLectures(addedTakenLectures)
			.build();
	}
}
