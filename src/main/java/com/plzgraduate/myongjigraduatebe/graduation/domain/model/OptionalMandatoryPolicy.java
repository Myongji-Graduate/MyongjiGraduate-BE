package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OptionalMandatoryPolicy {

	private final Long id;
	private final String name;
	private final String major;
	private final int requiredCount;
	private final int requiredCredit;
	private final List<CandidateLecture> candidateLectures;

	@Builder
	private OptionalMandatoryPolicy(Long id, String name, String major, int requiredCount,
		int requiredCredit, List<CandidateLecture> candidateLectures) {
		if (requiredCount <= 0) {
			throw new IllegalArgumentException("필요 선택 과목 수는 1개 이상이어야 합니다.");
		}
		if (requiredCredit < 0) {
			throw new IllegalArgumentException("필요 학점은 0 이상이어야 합니다.");
		}
		Objects.requireNonNull(candidateLectures, "선택필수 후보 과목 목록은 null일 수 없습니다.");
		long optionCount = candidateLectures.stream()
			.map(CandidateLecture::equivalenceKey)
			.distinct()
			.count();
		long lectureCount = candidateLectures.stream()
			.map(candidate -> candidate.lecture().getId())
			.distinct()
			.count();
		if (lectureCount != candidateLectures.size()) {
			throw new IllegalArgumentException("선택필수 후보에 중복된 학수번호가 있습니다.");
		}
		if (optionCount < requiredCount) {
			throw new IllegalArgumentException(
				"필요 선택 과목 수는 중복을 제거한 후보 수보다 클 수 없습니다.");
		}
		this.id = id;
		this.name = name;
		this.major = major;
		this.requiredCount = requiredCount;
		this.requiredCredit = requiredCredit;
		this.candidateLectures = List.copyOf(candidateLectures);
	}

	public record CandidateLecture(Lecture lecture, String equivalenceKey) {
		public CandidateLecture {
			Objects.requireNonNull(lecture, "선택필수 후보 과목은 null일 수 없습니다.");
			if (equivalenceKey == null || equivalenceKey.isBlank()) {
				throw new IllegalArgumentException("동등 과목 구분 키는 비어 있을 수 없습니다.");
			}
		}
	}
}
