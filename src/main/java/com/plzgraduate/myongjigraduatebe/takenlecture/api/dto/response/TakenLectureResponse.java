package com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLectureResponse {

	@Schema(name = "id", example = "135")
	private final Long id;
	@Schema(name = "year", example = "2023")
	private final String year;
	@Schema(name = "semester", example = "1학기")
	private final String semester;
	@Schema(name = "lectureCode", example = "HED01413")
	private final String lectureCode;
	@Schema(name = "lectureName", example = "캡스톤디자인")
	private final String lectureName;
	@Schema(name = "credit", example = "3")
	private final int credit;

	@Builder
	private TakenLectureResponse(Long id, String year, String semester, String lectureCode,
		String lectureName,
		int credit) {
		this.id = id;
		this.year = year;
		this.semester = semester;
		this.lectureCode = lectureCode;
		this.lectureName = lectureName;
		this.credit = credit;
	}

	public static TakenLectureResponse from(TakenLecture takenLecture) {
		return TakenLectureResponse.builder()
			.id(takenLecture.getId())
			.year(
				takenLecture.getYear() == 2099 ? "CUSTOM" : String.valueOf(takenLecture.getYear()))
			.semester(takenLecture.getSemester() == null ? "CUSTOM" : takenLecture.getSemester()
				.getName())
			.lectureCode(takenLecture.getLecture()
				.getLectureCode())
			.lectureName(takenLecture.getLecture()
				.getName())
			.credit(takenLecture.getLecture()
				.getCredit())
			.build();
	}
}
