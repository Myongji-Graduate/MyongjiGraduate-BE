package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class FindCompletedCreditAdapterTest extends PersistenceTestSupport {

	@Autowired
	private FindCompletedCreditAdapter findCompletedCreditAdapter;

	@DisplayName("졸업 카테고리에 해당하는 이수 학점이 존재하지 않을 시 예외가 발생한다.")
	@Test
	void findCategorizedCompletedCreditWithNonExistentCategory() {
		//given
		User user = User.builder()
			.id(1L).build();

		//when //then
		Assertions.assertThatThrownBy(() ->
				findCompletedCreditAdapter.findCategorizedCompletedCredit(user,
					GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이수 구분에 해당하지 않는 졸업 분류입니다.");

	}

}
