package com.plzgraduate.myongjigraduatebe.takenlecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.adapter.persistence.TakenLectureMapper;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class TakenLectureMapperTest extends PersistenceTestSupport {

	@Autowired
	private TakenLectureMapper takenLectureMapper;

	@DisplayName("JPA 엔티티를 도메인 엔티티로 변환한다.")
	@Test
	void mapToDomainEntity() {
		//given
		UserJpaEntity userJpaEntity = createUserJpaEntity(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영학과", null, StudentCategory.NORMAL);
		LectureJpaEntity lectureJpaEntity = createLectureJpaEntity(1L, "HEB01102", "기초프로그래밍", 3, 0, null);
		TakenLectureJpaEntity takenLectureJpaEntity = createTakenLectureJpaEntity(1L, userJpaEntity, lectureJpaEntity,
			2020, Semester.FIRST);

		//when
		TakenLecture takenLecture = takenLectureMapper.mapToDomainEntity(takenLectureJpaEntity);

		//then
		assertThat(takenLecture)
			.extracting("id", "year", "semester")
			.contains(1L, 2020, Semester.FIRST);

	}

	@DisplayName("도메인 엔티티를 JPA 엔티티로 변환하다.")
	@Test
	void mapToJpaEntity() {
		//given
		User user = createUser(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영학과", null, StudentCategory.NORMAL);
		Lecture lecture = createLecture(1L, "HEB01102", "기초프로그래밍", 3, 0, null);
		TakenLecture takenLecture = createTakenLecture(null, user, lecture, 2020, Semester.FIRST);

		//when
		TakenLectureJpaEntity takenLectureJpaEntity = takenLectureMapper.mapToJpaEntity(takenLecture);

		//then
		assertThat(takenLectureJpaEntity)
			.extracting("id", "year", "semester")
			.contains(null, 2020, Semester.FIRST);
	}

	@DisplayName("User: 도메인 엔티티를 JPA 엔티티로 변환하다.")
	@Test
	void mapToUserJpaEntity() {
		//given
		User user = createUser(null, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영학과", null, StudentCategory.NORMAL);

		//when
		UserJpaEntity userJpaEntity = takenLectureMapper.mapToUserJpaEntity(user);

		//then
		assertThat(userJpaEntity)
			.extracting("id", "authId", "password", "englishLevel", "name",
				"studentNumber", "entryYear", "major", "subMajor", "studentCategory")
			.contains(null, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
				"60211111", 21, "경영학과", null, StudentCategory.NORMAL);
	}

	private TakenLecture createTakenLecture(Long id, User user, Lecture lecture, int year, Semester semester) {
		return TakenLecture.builder()
			.id(id)
			.user(user)
			.lecture(lecture)
			.year(year)
			.semester(semester)
			.build();
	}

	private TakenLectureJpaEntity createTakenLectureJpaEntity(Long id, UserJpaEntity user, LectureJpaEntity lecture, int year, Semester semester) {
		return TakenLectureJpaEntity.builder()
			.id(id)
			.user(user)
			.lecture(lecture)
			.year(year)
			.semester(semester)
			.build();
	}

	private User createUser(Long id, String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return User.builder()
			.id(id)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}

	private UserJpaEntity createUserJpaEntity(Long id, String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return UserJpaEntity.builder()
			.id(id)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		return Lecture.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.duplicateCode(duplicateCode)
			.build();
	}

	private LectureJpaEntity createLectureJpaEntity(Long id, String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		return LectureJpaEntity.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.duplicateCode(duplicateCode)
			.build();
	}
}
