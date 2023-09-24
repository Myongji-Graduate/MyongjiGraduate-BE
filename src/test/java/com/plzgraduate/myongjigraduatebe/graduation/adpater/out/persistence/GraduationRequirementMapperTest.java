package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

@Import(GraduationRequirementMapper.class)
class GraduationRequirementMapperTest extends PersistenceTestSupport {

	@Autowired
	private GraduationRequirementMapper mapper;

	@DisplayName("졸업결과 jpa entitiy를 도메인 모델로 매핑한다.")
	@Test
	void mapToDomainModel() {
	    //given
		int totalCredit = 10;
		GraduationRequirementJpaEntity graduationRequirementJpaEntity = GraduationRequirementJpaEntity.builder()
			.totalCredit(totalCredit).build();

		//when
		GraduationRequirement graduationRequirement = mapper.mapToDomainModel(graduationRequirementJpaEntity);

		//then
		assertThat(graduationRequirement.getTotalCredit()).isEqualTo(totalCredit);
	}

}
