package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchLecture {
	private Long id;
	private String lectureCode;
	private String name;
	private int credit;
	private boolean isRevoked;

	@Builder
	private SearchLecture(Long id, String lectureCode, String name, int credit, boolean isRevoked) {
		this.id = id;
		this.lectureCode = lectureCode;
		this.name = name;
		this.credit = credit;
		this.isRevoked = isRevoked;
	}

	public static SearchLecture of(Lecture lecture) {
		return SearchLecture.builder()
			.id(lecture.getId())
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked() == 0)
			.build();
	}
}
