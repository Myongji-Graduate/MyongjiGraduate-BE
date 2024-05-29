package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchLectureResponse {

	@Schema(name = "id", example = "18")
	private final Long id;
	@Schema(name = "lectureCode", example = "KMA02137")
	private final String lectureCode;
	@Schema(name = "name", example = "4차산업혁명시대의진로선택")
	private final String name;
	@Schema(name = "credit", example = "2")
	private final int credit;
	@Schema(name = "isRevoked", example = "false")
	private final boolean revoked;
	@Schema(name = "isAddable", example = "true")
	private final boolean taken;

	@Builder
	private SearchLectureResponse(Long id, String lectureCode, String name, int credit, boolean revoked,
		boolean taken) {
		this.id = id;
		this.lectureCode = lectureCode;
		this.name = name;
		this.credit = credit;
		this.revoked = revoked;
		this.taken = taken;
	}

	public static SearchLectureResponse from(SearchedLectureDto searchedLectureDto) {
		return SearchLectureResponse.builder()
			.id(searchedLectureDto.getLecture().getId())
			.lectureCode(searchedLectureDto.getLecture().getLectureCode())
			.name(searchedLectureDto.getLecture().getName())
			.credit(searchedLectureDto.getLecture().getCredit())
			.revoked(searchedLectureDto.getLecture().getIsRevoked() != 0)
			.taken(searchedLectureDto.isAddable())
			.build();
	}
}
