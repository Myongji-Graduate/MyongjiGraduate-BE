package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasicAcademicalGraduationManagerTest {

	@DisplayName("동일과목 그룹에서 한 과목만 학문기초 학점으로 사용한다.")
	@Test
	void deduplicatesEquivalentTakenLectures() {
		Lecture oldLecture = Lecture.of("OLD", "구과목", 3, 0, "SAME");
		Lecture newLecture = Lecture.of("NEW", "신과목", 3, 0, "SAME");
		Set<BasicAcademicalCultureLecture> policies = Set.of(
			BasicAcademicalCultureLecture.of(oldLecture, "인문대")
		);
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.of(null, oldLecture, 2024, Semester.FIRST),
			TakenLecture.of(null, newLecture, 2025, Semester.FIRST)
		));
		BasicAcademicalGraduationManager manager =
			new DefaultBasicAcademicalGraduationManager();

		Set<TakenLecture> recognized =
			manager.findRecognizedTakenLectures(policies, inventory);

		assertThat(recognized).hasSize(1);
	}
}
