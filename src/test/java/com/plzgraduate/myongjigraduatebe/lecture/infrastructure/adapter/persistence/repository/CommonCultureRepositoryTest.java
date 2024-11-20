package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class CommonCultureRepositoryTest extends PersistenceTestSupport {

	private static final String BASIC_ENGLISH_CODE = "KMP02126";
	private static final String ENGLISH1_CODE = "KMA02106";
	private static final String ENGLISH2_CODE = "KMA02107";
	private static final String ENGLISH_CONVERSATION1_CODE = "KMA02108";
	private static final String ENGLISH_CONVERSATION2_CODE = "KMA02109";
	private static final String ENGLISH3_CODE = "KMA02123";
	private static final String ENGLISH4_CODE = "KMA02124";
	private static final String ENGLISH_CONVERSATION3_CODE = "KMA02125";
	private static final String ENGLISH_CONVERSATION4_CODE = "KMA02126";

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private CommonCultureRepository commonCultureRepository;

	@DisplayName("BASIC ENG: 유저의 입학년도가 적용 시작 년도, 적용 마감 년도 사이에 속하는 공통 교양 과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(ints = {16, 18, 20, 23})
	void findEngBasicAllByEntryYear(int entryYear) {
		//given
		saveEnglishLectures();

		//when
		List<CommonCultureJpaEntity> commonCultureGraduationLectures = commonCultureRepository.findEngBasicGraduationCommonCulturesByEntryYear(
			entryYear);

		//then
		assertThat(commonCultureGraduationLectures).hasSize(5)
			.extracting("startEntryYear")
			.contains(entryYear);

		List<String> lectureCodes = commonCultureGraduationLectures.stream()
			.map(commonCultureJpaEntity -> commonCultureJpaEntity.getLectureJpaEntity()
				.getId())
			.collect(Collectors.toList());
		assertThat(lectureCodes).contains(BASIC_ENGLISH_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH3_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH4_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH_CONVERSATION3_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH_CONVERSATION4_CODE);
	}

	@DisplayName("ENG12: 유저의 입학년도가 적용 시작 년도, 적용 마감 년도 사이에 속하는 공통 교양 과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(ints = {16, 18, 20, 23})
	void findEng12AllByEntryYear(int entryYears) {
		//given
		saveEnglishLectures();

		//when
		List<CommonCultureJpaEntity> commonCultureGraduationLectures =
			commonCultureRepository.findEng12GraduationCommonCulturesByEntryYear(entryYears);

		//then
		assertThat(commonCultureGraduationLectures).hasSize(4)
			.extracting("startEntryYear")
			.contains(entryYears);

		List<String> lectureCodes = commonCultureGraduationLectures.stream()
			.map(commonCultureJpaEntity -> commonCultureJpaEntity.getLectureJpaEntity()
				.getId())
			.collect(Collectors.toList());
		assertThat(lectureCodes).doesNotContain(BASIC_ENGLISH_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH3_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH4_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH_CONVERSATION3_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH_CONVERSATION4_CODE);
	}

	@DisplayName("ENG34: 유저의 입학년도가 적용 시작 년도, 적용 마감 년도 사이에 속하는 공통 교양 과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(ints = {16, 18, 20, 23})
	void findEng34AllAllByEntryYear(int entryYears) {
		//given
		saveEnglishLectures();

		//when
		List<CommonCultureJpaEntity> commonCultureGraduationLectures =
			commonCultureRepository.findEng34GraduationCommonCulturesByEntryYear(entryYears);

		//then
		assertThat(commonCultureGraduationLectures).hasSize(4)
			.extracting("startEntryYear")
			.contains(entryYears);

		List<String> lectureCodes = commonCultureGraduationLectures.stream()
			.map(commonCultureJpaEntity -> commonCultureJpaEntity.getLectureJpaEntity()
				.getId())
			.collect(Collectors.toList());
		assertThat(lectureCodes).doesNotContain(BASIC_ENGLISH_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH1_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH2_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH_CONVERSATION1_CODE);
		assertThat(lectureCodes).doesNotContain(ENGLISH_CONVERSATION2_CODE);
	}

	@DisplayName("ENGFREE: 유저의 입학년도가 적용 시작 년도, 적용 마감 년도 사이에 속하는 공통 교양 과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(ints = {16, 18, 20, 23})
	void findEngFreeAllAllByEntryYear(int entryYears) {
		//given
		saveEnglishLectures();

		//when
		List<CommonCultureJpaEntity> commonCultureGraduationLectures =
			commonCultureRepository.findEngFreeGraduationCommonCulturesByEntryYear(entryYears);

		//then
		assertThat(commonCultureGraduationLectures).hasSize(0);
	}

	private void saveEnglishLectures() {
		LectureJpaEntity 기초영어 = LectureJpaEntity.builder()
			.id(BASIC_ENGLISH_CODE)
			.build();
		LectureJpaEntity 영어1 = LectureJpaEntity.builder()
			.id(ENGLISH1_CODE)
			.build();
		LectureJpaEntity 영어2 = LectureJpaEntity.builder()
			.id(ENGLISH2_CODE)
			.build();
		LectureJpaEntity 영어3 = LectureJpaEntity.builder()
			.id(ENGLISH3_CODE)
			.build();
		LectureJpaEntity 영어4 = LectureJpaEntity.builder()
			.id(ENGLISH4_CODE)
			.build();
		LectureJpaEntity 영어회화1 = LectureJpaEntity.builder()
			.id(ENGLISH_CONVERSATION1_CODE)
			.build();
		LectureJpaEntity 영어회화2 = LectureJpaEntity.builder()
			.id(ENGLISH_CONVERSATION2_CODE)
			.build();
		LectureJpaEntity 영어회화3 = LectureJpaEntity.builder()
			.id(ENGLISH_CONVERSATION3_CODE)
			.build();
		LectureJpaEntity 영어회화4 = LectureJpaEntity.builder()
			.id(ENGLISH_CONVERSATION4_CODE)
			.build();
		List<LectureJpaEntity> englishLectures = List.of(기초영어, 영어1, 영어2, 영어3, 영어4, 영어회화1, 영어회화2,
			영어회화3, 영어회화4);
		lectureRepository.saveAll(englishLectures);

		for (LectureJpaEntity englishLecture : englishLectures) {
			CommonCultureJpaEntity commonCultureJpaEntityA = CommonCultureJpaEntity.builder()
				.lectureJpaEntity(englishLecture)
				.startEntryYear(16)
				.endEntryYear(17)
				.build();
			CommonCultureJpaEntity commonCultureJpaEntityB = CommonCultureJpaEntity.builder()
				.lectureJpaEntity(englishLecture)
				.startEntryYear(18)
				.endEntryYear(19)
				.build();
			CommonCultureJpaEntity commonCultureJpaEntityC = CommonCultureJpaEntity.builder()
				.lectureJpaEntity(englishLecture)
				.startEntryYear(20)
				.endEntryYear(22)
				.build();
			CommonCultureJpaEntity commonCultureJpaEntityD = CommonCultureJpaEntity.builder()
				.lectureJpaEntity(englishLecture)
				.startEntryYear(23)
				.endEntryYear(99)
				.build();
			commonCultureRepository.saveAll(
				List.of(commonCultureJpaEntityA, commonCultureJpaEntityB, commonCultureJpaEntityC,
					commonCultureJpaEntityD));
		}
	}
}
