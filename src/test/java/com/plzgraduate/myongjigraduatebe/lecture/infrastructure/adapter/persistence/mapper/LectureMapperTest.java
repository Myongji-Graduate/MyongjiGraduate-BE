package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CAREER;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.CULTURE_ART;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LectureMapperTest extends PersistenceTestSupport {

	@Autowired
	private LectureMapper lectureMapper;

	@DisplayName("CommonCulture: Jpa 엔티티를 도메인 모델로 변환한다.")
	@Test
	void mapToCommonCulture() {
		//given
		CommonCultureJpaEntity commonCultureJpaEntity = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(LectureJpaEntity.builder()
				.build())
			.commonCultureCategory(CAREER)
			.build();

		//when
		CommonCulture commonCulture = lectureMapper.mapToCommonCultureModel(commonCultureJpaEntity);

		//then
		assertThat(commonCulture.getCommonCultureCategory()).isEqualTo(CAREER);
	}

	@DisplayName("CoreCulture: Jpa 엔티티를 도메인 모델로 변환한다.")
	@Test
	void mapToCoreCulture() {
		//given
		CoreCultureJpaEntity commonCultureJpaEntity = CoreCultureJpaEntity.builder()
			.lectureJpaEntity(LectureJpaEntity.builder()
				.build())
			.coreCultureCategory(CULTURE_ART)
			.build();

		//when
		CoreCulture coreCulture = lectureMapper.mapToCoreCultureModel(commonCultureJpaEntity);

		//then
		assertThat(coreCulture.getCoreCultureCategory()).isEqualTo(CULTURE_ART);
	}

	@DisplayName("BasicAcademicalCulture: Jpa 엔티티를 도메인 모델로 변환한다.")
	@Test
	void mapToCBasicAcademicalCulture() {
		//given
		BasicAcademicalCultureLectureJpaEntity basicAcademicalCultureEntity = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(LectureJpaEntity.builder()
				.build())
			.college("인문대")
			.build();

		//when
		BasicAcademicalCultureLecture basicAcademicalCultureLecture = lectureMapper.mapToBasicAcademicalCultureLectureModel(
			basicAcademicalCultureEntity);

		//then
		assertThat(basicAcademicalCultureLecture.getCollege()).isEqualTo("인문대");
	}

	@DisplayName("Major: Jpa 엔티티를 도메인 모델로 변환한다.")
	@Test
	void mapToMajor() {
		//given
		MajorLectureJpaEntity majorEntity = MajorLectureJpaEntity.builder()
			.lectureJpaEntity(LectureJpaEntity.builder()
				.build())
			.major("응용소프트웨어")
			.build();

		//when
		MajorLecture basicAcademicalCulture = lectureMapper.mapToMajorLectureModel(majorEntity);

		//then
		assertThat(basicAcademicalCulture.getMajor()).isEqualTo("응용소프트웨어");
	}

}
