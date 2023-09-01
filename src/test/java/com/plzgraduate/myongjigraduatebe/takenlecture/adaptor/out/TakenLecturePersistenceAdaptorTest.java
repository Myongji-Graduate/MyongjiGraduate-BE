package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import({TakenLecturePersistenceAdaptor.class, TakenLectureMapper.class})
class TakenLecturePersistenceAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private TakenLecturePersistenceAdaptor takenLecturePersistenceAdaptor;

	@Autowired
	private TakenLectureRepository takenLectureRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LectureRepository lectureRepository;

	@DisplayName("수강과목을 저장한다.")
	@Test
	void saveTakenLectures() {
		//given
		User user = createUser(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영학과", null, StudentCategory.NORMAL);
		List<TakenLecture> takenLectures = new ArrayList<>();
		Lecture lecture1 = createLecture(1L, "KMA02104", "글쓰기", 3, 0, null);
		Lecture lecture2 = createLecture(2L, "KMA0210", "영어1", 2, 0, null);
		takenLectures.add(createTakenLecture(null, user, lecture1, 2020, Semester.FIRST));
		takenLectures.add(createTakenLecture(null, user, lecture2, 2020, Semester.SECOND));

		//when
		takenLecturePersistenceAdaptor.saveTakenLectures(takenLectures);
		//then
		List<TakenLectureJpaEntity> takenLectureJpaEntities = takenLectureRepository.findAll();
		//when
		assertThat(takenLectureJpaEntities.size()).isEqualTo(2);
	}

	@DisplayName("사용자의 수강과목 정보를 모두 삭제한다.")
	@Test
	void deleteTakenLecturesByUser() {
		//given
		User user = createUser(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영학과", null, StudentCategory.NORMAL);
		List<TakenLecture> takenLectures = new ArrayList<>();
		Lecture lecture1 = createLecture(1L, "KMA02104", "글쓰기", 3, 0, null);
		Lecture lecture2 = createLecture(2L, "KMA0210", "영어1", 2, 0, null);
		takenLectures.add(createTakenLecture(null, user, lecture1, 2020, Semester.FIRST));
		takenLectures.add(createTakenLecture(null, user, lecture2, 2020, Semester.SECOND));
		takenLecturePersistenceAdaptor.saveTakenLectures(takenLectures);
		//when
		takenLecturePersistenceAdaptor.deleteTakenLecturesByUser(user);
		//then
		List<TakenLectureJpaEntity> takenLectureJpaEntities = takenLectureRepository.findAll();
		assertThat(takenLectureJpaEntities.size()).isZero();
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
}
