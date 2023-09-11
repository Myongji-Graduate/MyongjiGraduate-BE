package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindTakenLectureResponse {
	private int totalCredit;
	private List<TakenLectureResponse> takenLectures;

	@Builder
	private FindTakenLectureResponse(int totalCredit, List<TakenLectureResponse> takenLectures) {
		this.totalCredit = totalCredit;
		this.takenLectures = takenLectures;
	}

	public static FindTakenLectureResponse from(int totalCredit, List<TakenLectureResponse> takenLectures) {
		return FindTakenLectureResponse.builder()
			.totalCredit(totalCredit)
			.takenLectures(takenLectures)
			.build();
	}
}
