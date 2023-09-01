package com.plzgraduate.myongjigraduatebe.lecture.adapter.out;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

@Component
public class LectureMapper {

	Lecture mapToLectureDomainEntity(LectureJpaEntity lecture) {

		return Lecture.builder()
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}

	LectureJpaEntity mapToLectureJpaEntity(Lecture lecture) {

		return LectureJpaEntity.builder()
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}
}
