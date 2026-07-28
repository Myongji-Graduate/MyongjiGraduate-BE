package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MandatoryMajorSpecialCaseHandler;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.ReplaceMandatoryMajorHandler;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("2026-1 기준 철학과 전공필수 특례는 더 이상 전필 목록을 주입하지 않는다.")
class ReplaceMandatoryMajorHandlerTest {

	private static final User user = UserFixture.철학과_20학번();
	private static final MajorType MAJOR_TYPE = MajorType.PRIMARY;
	private static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@Test
	void 철학과_전필_특례_비활성() {
		Set<Lecture> mandatoryLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01110"),
			mockLectureMap.get("HAI01111"),
			mockLectureMap.get("HAI01112"),
			mockLectureMap.get("HAI01566")
		));
		Set<Lecture> electiveLectures = new HashSet<>(Set.of(
			mockLectureMap.get("HAI01348"),
			mockLectureMap.get("HAI01247")
		));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());

		MandatoryMajorSpecialCaseHandler exceptionHandler = new ReplaceMandatoryMajorHandler();
		assertThat(exceptionHandler.evaluate(
			user, MAJOR_TYPE, takenLectureInventory, mandatoryLectures, electiveLectures,
			List.of())).isEmpty();
		assertThat(mandatoryLectures).containsExactlyInAnyOrder(
			mockLectureMap.get("HAI01110"),
			mockLectureMap.get("HAI01111"),
			mockLectureMap.get("HAI01112"),
			mockLectureMap.get("HAI01566")
		);
		assertThat(electiveLectures).containsExactlyInAnyOrder(
			mockLectureMap.get("HAI01348"),
			mockLectureMap.get("HAI01247")
		);
	}
}
