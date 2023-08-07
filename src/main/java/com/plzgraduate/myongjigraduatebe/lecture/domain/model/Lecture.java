package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Lecture {

	private final String lectureCode;
	private final String name;
	private final int credit;
	private final String duplicateCode;
	private final int isRevoked;

	@Builder
	private Lecture(String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		this.lectureCode = lectureCode;
		this.name = name;
		this.credit = credit;
		this.isRevoked = isRevoked;
		this.duplicateCode = duplicateCode;
	}

	public static Lecture of(String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		return Lecture.builder()
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.duplicateCode(duplicateCode)
			.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Lecture lecture = (Lecture)o;
		return Objects.equals(lectureCode, lecture.lectureCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lectureCode);
	}
}
