package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.update;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.update.UpdateTakenLectureCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTakenLectureRequest {

	@Schema(name = "deletedTakenLectures", example = "102, 2")
	private List<Long> deletedTakenLectures;

	@Schema(name = "addedTakenLectures", example = "103, 104")
	private List<Long> addedTakenLectures;

	@Builder
	private UpdateTakenLectureRequest(List<Long> deletedTakenLectures, List<Long> addedTakenLectures) {
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
