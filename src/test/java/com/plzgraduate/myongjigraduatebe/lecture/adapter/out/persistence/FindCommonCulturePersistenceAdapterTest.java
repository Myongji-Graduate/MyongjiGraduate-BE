package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import({LectureMapper.class, FindCommonCulturePersistenceAdapter.class})
class FindCommonCulturePersistenceAdapterTest extends PersistenceTestSupport {

	private static final String ENGLISH1_CODE = "KMA02106";
	private static final String ENGLISH_CONVERSATION3_CODE = "KMA02123";

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private CommonCultureRepository commonCultureRepository;
	@Autowired
	private FindCommonCulturePersistenceAdapter commonCulturePersistenceAdapter;

	@DisplayName("Eng12: 유저의 입학년도와 영어레벨에 해당하는 공통교양과목들을 조회한다.")
	@Test
	void findEng12CommonCulture() {
	    //given
		User eng12User = UserFixture.데이테크놀로지학과_18학번();
		createCommonCultures();

		//when
		Set<CommonCulture> commonCultures = commonCulturePersistenceAdapter.findCommonCulture(eng12User);

		//then
		assertThat(commonCultures).hasSize(1)
			.extracting("commonCultureCategory")
			.contains(ENGLISH);
	}

	@DisplayName("Eng12: 유저의 입학년도와 영어레벨에 해당하는 공통교양과목들을 조회한다.")
	@Test
	void findEng34CommonCulture() {
		//given
		User eng34User = UserFixture.데이테크놀로지학과_16학번_Eng34();
		createCommonCultures();

		//when
		Set<CommonCulture> commonCultures = commonCulturePersistenceAdapter.findCommonCulture(eng34User);

		//then
		assertThat(commonCultures).hasSize(1)
			.extracting("commonCultureCategory")
			.contains(ENGLISH);
	}

	@DisplayName("Eng12: 유저의 입학년도와 영어레벨에 해당하는 공통교양과목들을 조회한다.")
	@Test
	void findEngFreeCommonCulture() {
		//given
		User engFreeUser = UserFixture.응용소프트웨어학과_19학번_영어_면제();
		createCommonCultures();

		//when
		Set<CommonCulture> commonCultures = commonCulturePersistenceAdapter.findCommonCulture(engFreeUser);

		//then
		assertThat(commonCultures).hasSize(0);
	}

	private void createCommonCultures() {
		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.lectureCode(ENGLISH1_CODE)
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.lectureCode(ENGLISH_CONVERSATION3_CODE)
			.build();
		lectureRepository.saveAll(List.of(lectureJpaEntityA, lectureJpaEntityB));

		CommonCultureJpaEntity commonCultureJpaEntityA = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.commonCultureCategory(ENGLISH)
			.startEntryYear(18)
			.endEntryYear(23).build();
		CommonCultureJpaEntity commonCultureJpaEntityB = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityB)
			.commonCultureCategory(ENGLISH)
			.startEntryYear(16)
			.endEntryYear(17).build();
		commonCultureRepository.saveAll(List.of(commonCultureJpaEntityA, commonCultureJpaEntityB));
	}

}
