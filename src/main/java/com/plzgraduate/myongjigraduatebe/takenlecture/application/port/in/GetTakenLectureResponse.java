package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetTakenLectureResponse {
	private int totalCredit;
	private List<TakenLectureResponse> takenLectures;

	@Builder
	private GetTakenLectureResponse(int totalCredit, List<TakenLectureResponse> takenLectures) {
		this.totalCredit = totalCredit;
		this.takenLectures = takenLectures;
	}

	public static GetTakenLectureResponse from(List<TakenLectureResponse> takenLectures) {
		int totalCredit = takenLectures.stream().mapToInt(TakenLectureResponse::getCredit).sum();
		return GetTakenLectureResponse.builder()
			.totalCredit(totalCredit)
			.takenLectures(takenLectures)
			.build();
	}
}
