package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.CULTURE_ART;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.SOCIETY_COMMUNITY;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCoreCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class FindCoreCulturePersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private CoreCultureRepository coreCultureRepository;
	@Autowired
	private FindCoreCulturePersistenceAdapter coreCulturePersistenceAdapter;


	@DisplayName("유저의 입학년도에 해당하는 핵심교양과목들을 조회한다.")
	@Test
	void findCoreCulture() {
		//given
		User user = UserFixture.응용소프트웨어학과_19학번();
		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.lectureCode("testA")
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.lectureCode("testB")
			.build();
		lectureRepository.saveAll(List.of(lectureJpaEntityA, lectureJpaEntityB));

		CoreCultureJpaEntity coreCultureJpaEntityA = CoreCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.coreCultureCategory(CULTURE_ART)
			.startEntryYear(16)
			.endEntryYear(18).build();
		CoreCultureJpaEntity coreCultureJpaEntityB = CoreCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.coreCultureCategory(SOCIETY_COMMUNITY)
			.startEntryYear(19)
			.endEntryYear(23).build();
		coreCultureRepository.saveAll(List.of(coreCultureJpaEntityA, coreCultureJpaEntityB));

		//when
		Set<CoreCulture> commonCultures = coreCulturePersistenceAdapter.findCoreCulture(user);

		//then
		assertThat(commonCultures).hasSize(1)
			.extracting("coreCultureCategory")
			.contains(SOCIETY_COMMUNITY);

	}

}
