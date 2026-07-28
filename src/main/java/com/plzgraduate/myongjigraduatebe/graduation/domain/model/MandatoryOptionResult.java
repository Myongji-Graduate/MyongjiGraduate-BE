package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;

public record MandatoryOptionResult(
	String name,
	int requiredCount,
	int takenCount,
	List<Lecture> candidateLectures
) {
	public MandatoryOptionResult {
		candidateLectures = List.copyOf(candidateLectures);
	}
}
