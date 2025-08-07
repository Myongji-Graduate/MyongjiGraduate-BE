package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLecture implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Long id;
	private final User user;
	private final Lecture lecture;
	private final Integer year;
	private final Semester semester;
	private final Instant createdAt;

	@Builder
	private TakenLecture(
		Long id,
		User user,
		Lecture lecture,
		Integer year,
		Semester semester,
		Instant createdAt
	) {
		this.id = id;
		this.user = user;
		this.lecture = lecture;
		this.year = year;
		this.semester = semester;
		this.createdAt = createdAt;
	}

	public static TakenLecture of(User user, Lecture lecture, Integer year, Semester semester) {
		return TakenLecture.builder()
			.user(user)
			.lecture(lecture)
			.year(year)
			.semester(semester)
			.build();
	}

	public static TakenLecture custom(User user, Lecture lecture) {
		return TakenLecture.builder()
			.user(user)
			.lecture(lecture)
			.year(2099)
			.build();
	}

	@Override
	public String toString() {
		return "TakenLecture{" +
			"id=" + id +
			", user=" + user +
			", lecture=" + lecture +
			", year=" + year +
			", semester=" + semester +
			", createdAt=" + createdAt +
			'}';
	}

	public boolean takenAfter(int year) {
		return this.year >= year;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TakenLecture that = (TakenLecture) o;
		return Objects.equals(user, that.user) && Objects.equals(lecture, that.lecture) &&
			Objects.equals(year, that.year) && Objects.equals(semester, that.semester);
	}

	@Override
	public int hashCode() {
		return Objects.hash(user, lecture, year, semester);
	}
}
