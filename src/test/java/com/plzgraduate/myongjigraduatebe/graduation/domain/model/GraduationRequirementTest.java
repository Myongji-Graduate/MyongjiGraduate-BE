package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GraduationRequirementTest {

	@DisplayName("공통교양의 영어카테고리 학점(6점)을 일반교양 학점으로 이관한다.")
	@Test
	void transferEnglishCategoryCredit() {
		//given
		int beforeTransferCommonCultureCredit = 10;
		int beforeTransferNormalCultureCredit = 0;
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.commonCultureCredit(beforeTransferCommonCultureCredit)
			.normalCultureCredit(beforeTransferNormalCultureCredit).build();

		//when
		graduationRequirement.transferEnglishCategoryCredit();

		//then
		assertThat(graduationRequirement.getCommonCultureCredit())
			.isEqualTo(beforeTransferCommonCultureCredit - ENGLISH.getTotalCredit());
		assertThat(graduationRequirement.getNormalCultureCredit())
			.isEqualTo(beforeTransferNormalCultureCredit + ENGLISH.getTotalCredit());
	}

}
