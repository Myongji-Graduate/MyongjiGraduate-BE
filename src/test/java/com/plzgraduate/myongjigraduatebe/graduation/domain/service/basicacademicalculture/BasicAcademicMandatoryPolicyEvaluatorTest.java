package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BasicAcademicMandatoryPolicyEvaluator.Evaluation;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class BasicAcademicMandatoryPolicyEvaluatorTest {

	private static final Lecture ECONOMICS = lecture("KMD02123", "경제학원론");
	private static final Lecture BUSINESS = lecture("KMD02192", "경영학입문");

	@Test
	void satisfiesChooseOneWithOneTakenRecognitionGroup() {
		OptionalMandatoryPolicy policy = policy(1);
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.builder().lecture(ECONOMICS).build()
		));

		Evaluation result = BasicAcademicMandatoryPolicyEvaluator.evaluate(
			List.of(policy), inventory);

		assertThat(result.satisfied()).isTrue();
		assertThat(result.remainingCandidates()).isEmpty();
	}

	@Test
	void reportsCandidatesWhenRequiredChoiceIsMissing() {
		Evaluation result = BasicAcademicMandatoryPolicyEvaluator.evaluate(
			List.of(policy(1)), TakenLectureInventory.from(Set.of()));

		assertThat(result.satisfied()).isFalse();
		assertThat(result.remainingCandidates()).containsExactly(ECONOMICS, BUSINESS);
	}

	@Test
	void equivalentLecturesCountAsOneChoice() {
		Lecture oldEconomics = Lecture.builder()
			.id("OLD")
			.name("구 경제학원론")
			.credit(3)
			.duplicateCode("KMD02123")
			.build();
		OptionalMandatoryPolicy policy = OptionalMandatoryPolicy.builder()
			.name("모두필수")
			.major("응용통계학전공")
			.requiredCount(2)
			.requiredCredit(6)
			.candidateLectures(List.of(
				new CandidateLecture(ECONOMICS, "ECONOMICS"),
				new CandidateLecture(oldEconomics, "ECONOMICS"),
				new CandidateLecture(BUSINESS, "BUSINESS")
			))
			.build();
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.builder().lecture(ECONOMICS).build(),
			TakenLecture.builder().lecture(oldEconomics).build()
		));

		Evaluation result = BasicAcademicMandatoryPolicyEvaluator.evaluate(
			List.of(policy), inventory);

		assertThat(result.satisfied()).isFalse();
		assertThat(result.remainingCandidates()).containsExactly(BUSINESS);
	}

	private static OptionalMandatoryPolicy policy(int requiredCount) {
		return OptionalMandatoryPolicy.builder()
			.name("택일")
			.major("응용통계학전공")
			.requiredCount(requiredCount)
			.requiredCredit(requiredCount * 3)
			.candidateLectures(List.of(
				new CandidateLecture(ECONOMICS, "ECONOMICS"),
				new CandidateLecture(BUSINESS, "BUSINESS")
			))
			.build();
	}

	private static Lecture lecture(String id, String name) {
		return Lecture.builder()
			.id(id)
			.name(name)
			.credit(3)
			.duplicateCode(id)
			.build();
	}
}
