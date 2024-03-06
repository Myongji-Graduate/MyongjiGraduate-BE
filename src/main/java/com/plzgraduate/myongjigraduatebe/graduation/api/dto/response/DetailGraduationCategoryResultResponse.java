package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationCategoryResultResponse {

	@Schema(name = "categoryName", example = "공통교양(기독교)")
	private final String categoryName;
	@Schema(name = "totalCredits", example = "4")
	private final int totalCredits;
	@Schema(name = "takenCredits", example = "4")
	private final int takenCredits;
	private final List<LectureResponse> takenLectures;
	private final List<LectureResponse> haveToLectures;
	@Schema(name = "completed", example = "true")
	private final boolean completed;

	@Builder
	private DetailGraduationCategoryResultResponse(String categoryName, int totalCredits, int takenCredits,
		List<LectureResponse> takenLectures, List<LectureResponse> haveToLectures, boolean completed) {
		this.categoryName = categoryName;
		this.totalCredits = totalCredits;
		this.takenCredits = takenCredits;
		this.takenLectures = takenLectures;
		this.haveToLectures = haveToLectures;
		this.completed = completed;
	}

	public static DetailGraduationCategoryResultResponse from(DetailCategoryResult detailCategoryResult) {
		return DetailGraduationCategoryResultResponse.builder()
			.categoryName(detailCategoryResult.getDetailCategoryName())
			.totalCredits(detailCategoryResult.getTotalCredits())
			.takenCredits(detailCategoryResult.getTakenCredits())
			.takenLectures(detailCategoryResult.getTakenLectures().stream()
				.map(LectureResponse::from)
				.collect(Collectors.toList()))
			.haveToLectures(detailCategoryResult.getHaveToLectures().stream()
				.map(LectureResponse::from)
				.collect(Collectors.toList()))
			.completed(detailCategoryResult.isCompleted()).build();
	}
}
