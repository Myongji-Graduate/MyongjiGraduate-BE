package com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindTakenLectureResponse {

	@Schema(name = "totalCredit", example = "115")
	private int totalCredit;
	private List<TakenLectureResponse> takenLectures;

	@Builder
	private FindTakenLectureResponse(int totalCredit, List<TakenLectureResponse> takenLectures) {
		this.totalCredit = totalCredit;
		this.takenLectures = takenLectures;
	}

	public static FindTakenLectureResponse of(int totalCredit, List<TakenLectureResponse> takenLectures) {
		return FindTakenLectureResponse.builder()
			.totalCredit(totalCredit)
			.takenLectures(takenLectures)
			.build();
	}
}
