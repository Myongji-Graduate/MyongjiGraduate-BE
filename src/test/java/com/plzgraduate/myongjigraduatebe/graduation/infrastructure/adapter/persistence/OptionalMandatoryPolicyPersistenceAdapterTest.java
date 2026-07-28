package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity.OptionalMandatoryPolicyJpaEntity.PolicyStatus;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.repository.OptionalMandatoryPolicyRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptionalMandatoryPolicyPersistenceAdapterTest {

	@Mock
	private OptionalMandatoryPolicyRepository repository;
	@Mock
	private LectureMapper lectureMapper;
	@InjectMocks
	private OptionalMandatoryPolicyPersistenceAdapter sut;

	@DisplayName("국제통상학전공 조회 시 국제통상학과 alias도 함께 조회한다.")
	@Test
	void findActivePolicies_alias() {
		when(repository.findActivePolicies(
			eq(List.of("국제통상학전공", "국제통상학과")),
			eq(25),
			eq(MajorType.PRIMARY),
			any(),
			eq(PolicyStatus.ACTIVE)
		)).thenReturn(List.of());

		assertThat(sut.findActivePolicies("국제통상학전공", 25, MajorType.PRIMARY)).isEmpty();

		verify(repository).findActivePolicies(
			eq(List.of("국제통상학전공", "국제통상학과")),
			eq(25),
			eq(MajorType.PRIMARY),
			any(),
			eq(PolicyStatus.ACTIVE)
		);
	}

	@DisplayName("국제통상학전공 기본학문기초 조회 시 국제통상학과 alias도 함께 조회한다.")
	@Test
	void findActiveBasicPolicies_alias() {
		when(repository.findActiveBasicPolicies(
			eq(List.of("국제통상학전공", "국제통상학과")),
			eq(25),
			eq(MajorType.PRIMARY),
			any(),
			eq(PolicyStatus.ACTIVE)
		)).thenReturn(List.of());

		assertThat(sut.findActiveBasicPolicies("국제통상학전공", 25, MajorType.PRIMARY)).isEmpty();

		verify(repository).findActiveBasicPolicies(
			eq(List.of("국제통상학전공", "국제통상학과")),
			eq(25),
			eq(MajorType.PRIMARY),
			any(),
			eq(PolicyStatus.ACTIVE)
		);
	}
}
