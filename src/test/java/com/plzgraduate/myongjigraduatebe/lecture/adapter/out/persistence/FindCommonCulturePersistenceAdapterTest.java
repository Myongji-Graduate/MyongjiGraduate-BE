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

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private CommonCultureRepository commonCultureRepository;
	@Autowired
	private FindCommonCulturePersistenceAdapter commonCulturePersistenceAdapter;

	@DisplayName("유저의 입학년도에 해당하는 공통교양과목들을 조회한다.")
	@Test
	void findCommonCulture() {
	    //given
		User user = UserFixture.응용소프트웨어학과_19학번();
		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.lectureCode("testA")
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.lectureCode("testB")
			.build();
		lectureRepository.saveAll(List.of(lectureJpaEntityA, lectureJpaEntityB));

		CommonCultureJpaEntity commonCultureJpaEntityA = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.commonCultureCategory(CAREER)
			.startEntryYear(16)
			.endEntryYear(18).build();
		CommonCultureJpaEntity commonCultureJpaEntityB = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityB)
			.commonCultureCategory(EXPRESSION)
			.startEntryYear(19)
			.endEntryYear(23).build();
		commonCultureRepository.saveAll(List.of(commonCultureJpaEntityA, commonCultureJpaEntityB));

		//when
		Set<CommonCulture> commonCultures = commonCulturePersistenceAdapter.findCommonCulture(user);

		//then
		assertThat(commonCultures).hasSize(1)
			.extracting("commonCultureCategory")
			.contains(EXPRESSION);
	}

}
