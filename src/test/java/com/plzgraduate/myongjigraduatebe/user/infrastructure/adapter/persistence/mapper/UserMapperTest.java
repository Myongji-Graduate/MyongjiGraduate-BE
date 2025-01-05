package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.*;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserMapperTest extends PersistenceTestSupport {

	@Autowired
	private UserMapper userMapper;

	@DisplayName("JPA 엔티티를 도메인 엔티티로 변환한다.")
	@Test
	void mapToDomainEntityTest() {
		//given
		UserJpaEntity userJpaEntity = createUserJpaEntity();

		//when
		User user = userMapper.mapToDomainEntity(userJpaEntity);

		//then
		assertThat(user)
			.extracting("id", "authId", "password", "englishLevel", "name", "studentNumber",
				"entryYear",
				"primaryMajor", "subMajor", "studentCategory", "totalCredit", "takenCredit",
				"graduated")
			.contains(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
				"60211111", 21, "경영", null, StudentCategory.NORMAL, 100, 40.0, false);
		assertThat(user.getTransferCredit())
				.extracting("normalCulture", "majorLecture", "freeElective", "christianLecture")
				.containsExactly(0, 0, 0, 0);
		assertThat(user.getExchangeCredit())
				.extracting("basicAcademicalCulture", "normalCulture", "major", "dualBasicAcademicalCulture", "dualMajor", "fusionMajor", "subMajor", "freeElective")
				.containsExactly(0, 0, 0, 0, 0, 0, 0, 0);

	}

	@DisplayName("도메인 엔티티를 JPA 엔티티 변환한다.")
	@Test
	void mapToJpaEntityTest() {
		//given
		User user = createUser();

		//when
		UserJpaEntity userJpaEntity = userMapper.mapToJpaEntity(user);

		//then
		assertThat(userJpaEntity)
			.extracting("id", "authId", "password", "englishLevel", "name", "studentNumber",
				"entryYear", "major",
				"subMajor", "studentCategory", "totalCredit", "takenCredit", "graduated")
			.contains(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
				"60211111", 21, "경영", null, StudentCategory.NORMAL, 100, 40.0, false);
		assertThat(userJpaEntity.getTransferCredit())
				.isEqualTo("0/0/0/0");
		assertThat(userJpaEntity.getExchangeCredit())
				.isEqualTo("0/0/0/0/0/0/0/0");
	}

	private User createUser() {
		return User.builder()
			.id(1L)
			.authId("mju1000")
			.password("mju1000!")
			.name("김명지")
			.studentNumber("60211111")
			.entryYear(21)
			.englishLevel(EnglishLevel.ENG12)
			.primaryMajor("경영")
			.dualMajor(null)
			.subMajor(null)
			.transferCredit(TransferCredit.from("0/0/0/0"))
			.exchangeCredit(ExchangeCredit.from("0/0/0/0/0/0/0/0"))
			.totalCredit(100)
			.takenCredit(40)
			.graduated(false)
			.studentCategory(StudentCategory.NORMAL)
			.build();
	}

	private UserJpaEntity createUserJpaEntity() {
		return UserJpaEntity.builder()
				.id(1L)
				.authId("mju1000")
				.password("mju1000!")
				.name("김명지")
				.studentNumber("60211111")
				.entryYear(21)
				.englishLevel(EnglishLevel.ENG12)
				.major("경영")
				.dualMajor(null) // 복수전공을 null로 설정
				.subMajor(null)
				.associatedMajor(null)
				.transferCredit("0/0/0/0") // 기본값 설정
				.exchangeCredit("0/0/0/0/0/0/0/0") //기본값 설정
				.studentCategory(StudentCategory.NORMAL)
				.totalCredit(100)
				.takenCredit(40.0)
				.graduated(false)
				.build();
	}
}
