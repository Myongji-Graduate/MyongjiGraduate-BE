package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.SearchedLectureDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchLectureResponse {

	@Schema(name = "id", example = "KMA02137")
	private final String id;
	@Schema(name = "name", example = "4차산업혁명시대의진로선택")
	private final String name;
	@Schema(name = "credit", example = "2")
	private final int credit;
	@Schema(name = "isRevoked", example = "false")
	private final boolean revoked;
	@Schema(name = "isAddable", example = "true")
	private final boolean taken;

	@Builder
	private SearchLectureResponse(String id, String name, int credit, boolean revoked, boolean taken) {
		this.id = id;
		this.name = name;
		this.credit = credit;
		this.revoked = revoked;
		this.taken = taken;
	}

	public static SearchLectureResponse from(SearchedLectureDto searchedLectureDto) {
		return SearchLectureResponse.builder()
			.id(searchedLectureDto.getLecture()
				.getId())
			.name(searchedLectureDto.getLecture()
				.getName())
			.credit(searchedLectureDto.getLecture()
				.getCredit())
			.revoked(searchedLectureDto.getLecture()
				.getIsRevoked() != 0)
			.taken(searchedLectureDto.isAddable())
			.build();
	}
}
