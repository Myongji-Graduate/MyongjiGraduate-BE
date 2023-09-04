package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

@Import(LectureMapper.class)
class LectureMapperTest extends PersistenceTestSupport {

	@Autowired
	private LectureMapper lectureMapper;

	@DisplayName("CommonCulture: Jpa 엔티티를 도메인 모델로 변환한다.")
	@Test
	void mapToCommonCulture() {
		//given
		CommonCultureJpaEntity commonCultureJpaEntity = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(LectureJpaEntity.builder().build())
			.commonCultureCategory(CAREER).build();

		//when
		CommonCulture commonCulture = lectureMapper.mapToDomainCommonCultureModel(commonCultureJpaEntity);

		//then
		assertThat(commonCulture.getCommonCultureCategory()).isEqualTo(CAREER);
	}

	@DisplayName("CoreCulture: Jpa 엔티티를 도메인 모델로 변환한다.")
	@Test
	void mapToCoreCulture() {
		//given
		CoreCultureJpaEntity commonCultureJpaEntity = CoreCultureJpaEntity.builder()
			.lectureJpaEntity(LectureJpaEntity.builder().build())
			.coreCultureCategory(CULTURE_ART).build();

		//when
		CoreCulture coreCulture = lectureMapper.mapToDomainCoreCultureModel(commonCultureJpaEntity);

		//then
		assertThat(coreCulture.getCoreCultureCategory()).isEqualTo(CULTURE_ART);
	}

}
