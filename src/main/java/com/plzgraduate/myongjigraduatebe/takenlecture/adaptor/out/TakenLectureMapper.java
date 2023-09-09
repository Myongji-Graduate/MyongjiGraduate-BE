package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Component
class TakenLectureMapper {

	TakenLecture mapToDomainEntity(TakenLectureJpaEntity takenLecture) {

		return TakenLecture.builder()
			.id(takenLecture.getId())
			.user(mapToUserDomainEntity(takenLecture.getUser()))
			.lecture(mapToLectureDomainEntity(takenLecture.getLecture()))
			.year(takenLecture.getYear())
			.semester(takenLecture.getSemester())
			.build();
	}

	TakenLectureJpaEntity mapToJpaEntity(TakenLecture takenLecture) {

		return TakenLectureJpaEntity.builder()
			.id(takenLecture.getId())
			.user(mapToUserJpaEntity(takenLecture.getUser()))
			.lecture(mapToLectureJpaEntity(takenLecture.getLecture()))
			.year(takenLecture.getYear())
			.semester(takenLecture.getSemester())
			.build();
	}

	private User mapToUserDomainEntity(UserJpaEntity user) {

		return User.builder()
			.id(user.getId())
			.authId(user.getAuthId())
			.password(user.getPassword())
			.name(user.getName())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.englishLevel(user.getEnglishLevel())
			.major(user.getMajor())
			.subMajor(user.getSubMajor())
			.studentCategory(user.getStudentCategory())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}

	UserJpaEntity mapToUserJpaEntity(User user) {

		return UserJpaEntity.builder()
			.id(user.getId())
			.authId(user.getAuthId())
			.password(user.getPassword())
			.name(user.getName())
			.englishLevel(user.getEnglishLevel())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.major(user.getMajor())
			.subMajor(user.getSubMajor())
			.studentCategory(user.getStudentCategory())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}

	private Lecture mapToLectureDomainEntity(LectureJpaEntity lecture) {

		return Lecture.builder()
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}

	private LectureJpaEntity mapToLectureJpaEntity(Lecture lecture) {

		return LectureJpaEntity.builder()
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}
}
