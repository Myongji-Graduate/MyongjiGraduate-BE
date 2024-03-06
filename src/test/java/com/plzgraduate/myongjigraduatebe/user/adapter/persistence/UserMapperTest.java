package com.plzgraduate.myongjigraduatebe.user.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.adapter.out.persistence.mapper.UserMapper;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class UserMapperTest extends PersistenceTestSupport {

	@Autowired
	private UserMapper userMapper;

	@DisplayName("JPA 엔티티를 도메인 엔티티로 변환한다.")
	@Test
	void mapToDomainEntityTest() {
		//given
		UserJpaEntity userJpaEntity = createUserJpaEntity(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영", null, StudentCategory.NORMAL);

		//when
		User user = userMapper.mapToDomainEntity(userJpaEntity);

		//then
		assertThat(user)
			.extracting("id", "authId", "password", "englishLevel", "name",
				"studentNumber", "entryYear", "major", "subMajor", "studentCategory")
			.contains(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
				"60211111", 21, "경영", null, StudentCategory.NORMAL);
	}

	@DisplayName("도메인 엔티티를 JPA 엔티티 변환한다.")
	@Test
	void mapToJpaEntityTest() {
		//given
		User user = createUser("mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
			"60211111", 21, "경영", null, StudentCategory.NORMAL);

		//when
		UserJpaEntity userJpaEntity = userMapper.mapToJpaEntity(user);

		//then
		assertThat(userJpaEntity)
			.extracting("id", "authId", "password", "englishLevel", "name",
				"studentNumber", "entryYear", "major", "subMajor", "studentCategory")
			.contains(1L, "mju1000", "mju1000!", EnglishLevel.ENG12, "김명지",
				"60211111", 21, "경영", null, StudentCategory.NORMAL);
	}

	private User createUser(String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return User.builder()
			.id(1L)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}

	private UserJpaEntity createUserJpaEntity(Long id, String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return UserJpaEntity.builder()
			.id(id)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
