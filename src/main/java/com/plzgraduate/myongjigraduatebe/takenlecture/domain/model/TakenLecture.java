package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import java.util.Objects;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLecture {

	private final Long id;
	private final User user;
	private final Lecture lecture;
	private final int year;
	private final Semester semester;

	@Builder
	private TakenLecture(Long id, User user, Lecture lecture, int year, Semester semester) {
		this.id = id;
		this.user = user;
		this.lecture = lecture;
		this.year = year;
		this.semester = semester;
	}

	public static TakenLecture of(User user, Lecture lecture, int year, Semester semester) {
		return TakenLecture.builder()
			.user(user)
			.lecture(lecture)
			.year(year)
			.semester(semester)
			.build();
	}

	public boolean takenAfter(int year) {
		return this.year >= year;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TakenLecture that = (TakenLecture)o;
		return Objects.equals(user, that.user) && Objects.equals(lecture, that.lecture) &&
			Objects.equals(year, that.year) && Objects.equals(semester, that.semester);
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, lecture, year, semester);
	}
}
