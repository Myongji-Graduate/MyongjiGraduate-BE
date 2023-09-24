package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationCategoryResultResponse {

	private final String categoryName;
	private final int totalCredits;
	private final int takenCredits;
	private final List<LectureResponse> takenLectures;
	private final List<LectureResponse> haveToLectures;
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
