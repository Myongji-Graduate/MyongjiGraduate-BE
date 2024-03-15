package com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.response;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

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

	public static FindTakenLectureResponse from(TakenLectureInventory takenLectureInventory) {
		return FindTakenLectureResponse.builder()
			.totalCredit(takenLectureInventory.calculateTotalCredit())
			.takenLectures(takenLectureInventory.getTakenLectures().stream()
				.sorted(Comparator.comparing(TakenLecture::getYear, Collections.reverseOrder())
					.thenComparing(TakenLecture::getSemester, Comparator.nullsFirst(Comparator.reverseOrder()))
					.thenComparing(TakenLecture::getCreatedAt, Comparator.reverseOrder()))
				.map(TakenLectureResponse::from)
				.collect(Collectors.toList()))
			.build();
	}
}
