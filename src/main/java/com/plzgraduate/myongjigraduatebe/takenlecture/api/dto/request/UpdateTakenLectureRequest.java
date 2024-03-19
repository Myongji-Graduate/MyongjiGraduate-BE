package com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request;

import java.util.List;

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

}
