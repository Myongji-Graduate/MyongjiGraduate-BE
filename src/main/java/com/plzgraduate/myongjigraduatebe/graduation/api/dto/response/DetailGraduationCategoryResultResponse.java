package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationCategoryResultResponse {

	@Schema(name = "categoryName", example = "공통교양(기독교)")
	private final String categoryName;
	@Schema(name = "totalCredit", example = "4")
	private final int totalCredit;
	@Schema(name = "takenCredit", example = "4")
	private final int takenCredit;
	private final List<LectureResponse> takenLectures;
	private final List<LectureResponse> haveToLectures;
	private final List<LectureResponse> mandatoryLectures;
	private final List<MandatoryOptionResponse> mandatoryOptions;
	@Schema(name = "completed", example = "true")
	private final boolean completed;

	@Builder
	private DetailGraduationCategoryResultResponse(
		String categoryName,
		int totalCredit,
		int takenCredit,
		List<LectureResponse> takenLectures,
		List<LectureResponse> haveToLectures,
		List<LectureResponse> mandatoryLectures,
		List<MandatoryOptionResponse> mandatoryOptions,
		boolean completed
	) {
		this.categoryName = categoryName;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.takenLectures = takenLectures;
		this.haveToLectures = haveToLectures;
		this.mandatoryLectures = mandatoryLectures;
		this.mandatoryOptions = mandatoryOptions;
		this.completed = completed;
	}

	public static DetailGraduationCategoryResultResponse from(
		DetailCategoryResult detailCategoryResult) {
		return DetailGraduationCategoryResultResponse.builder()
			.categoryName(detailCategoryResult.getDetailCategoryName())
			.totalCredit(detailCategoryResult.getTotalCredits())
			.takenCredit(detailCategoryResult.getTakenCredits())
			.takenLectures(detailCategoryResult.getTakenLectures()
				.stream()
				.map(LectureResponse::from)
				.collect(Collectors.toList()))
			.haveToLectures(detailCategoryResult.getHaveToLectures()
				.stream()
				.map(LectureResponse::from)
				.collect(Collectors.toList()))
			.mandatoryLectures(detailCategoryResult.getMandatoryLectures()
				.stream()
				.map(LectureResponse::from)
				.collect(Collectors.toList()))
			.mandatoryOptions(detailCategoryResult.getMandatoryOptions().stream()
				.map(MandatoryOptionResponse::from)
				.toList())
			.completed(detailCategoryResult.isCompleted())
			.build();
	}

	public record MandatoryOptionResponse(
		String name,
		int requiredCount,
		int takenCount,
		List<LectureResponse> candidates
	) {
		static MandatoryOptionResponse from(
			com.plzgraduate.myongjigraduatebe.graduation.domain.model.MandatoryOptionResult result) {
			return new MandatoryOptionResponse(
				result.name(),
				result.requiredCount(),
				result.takenCount(),
				result.candidateLectures().stream().map(LectureResponse::from).toList()
			);
		}
	}
}
