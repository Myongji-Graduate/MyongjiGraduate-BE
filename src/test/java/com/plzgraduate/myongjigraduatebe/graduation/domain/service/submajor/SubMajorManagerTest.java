package com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.SUB_MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class SubMajorManagerTest {

	@DisplayName("부전공 학생의 부전공 졸업 결과를 생성한다.")
	@Test
	void createDetailGraduationResult() {
		//given
		User user = UserFixture.응용소프트웨어학과_19학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01313").credit(3).build(), 2019, Semester.FIRST),
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01310").credit(3).build(), 2020, Semester.FIRST),
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01309").credit(3).build(), 2021, Semester.FIRST),
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01306").credit(3).build(), 2021, Semester.SECOND),
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01304").credit(3).build(), 2022, Semester.FIRST),
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01212").credit(3).build(), 2023, Semester.FIRST),
			TakenLecture.of(user, Lecture.builder().lectureCode("HEC01209").credit(3).build(), 2023, Semester.SECOND)
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<MajorLecture> graduationLectures = lectureSet();
		int subMajorGraduationCredit = 21;
		int takenLecturesCount = takenLectures.size();

		GraduationManager<MajorLecture> subMajorManager = new SubMajorManager();

		//when
		DetailGraduationResult detailGraduationResult = subMajorManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationLectures, subMajorGraduationCredit);

		//then
		int majorCredit = 3;
		assertThat(detailGraduationResult.getDetailCategory()).hasSize(1)
			.extracting("detailCategoryName", "isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(
				tuple("전공선택", true, true, subMajorGraduationCredit, majorCredit * takenLecturesCount));
		assertThat(detailGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(SUB_MAJOR, true, subMajorGraduationCredit, (double)majorCredit * takenLecturesCount);

	}

	private Set<MajorLecture> lectureSet() {
		Set<MajorLecture> lectureSet = new HashSet<>();
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01313"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01310"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01309"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01306"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01304"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01212"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01209"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01211"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01203"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01206"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01301"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01302"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01401"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01314"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01305"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01204"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01210"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01207"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01205"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01202"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01208"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEB01103"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEB01101"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01405"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01307"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01311"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01318"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01402"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01406"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01317"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01316"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01303"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01201"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01408"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01404"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01407"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01315"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01403"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01308"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01312"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01409"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01410"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01411"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01412"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01413"), "응용소프트웨어전공", 0, 16, 99));
		lectureSet.add(MajorLecture.of(Lecture.from("HEC01414"), "응용소프트웨어전공", 0, 16, 99));
		return lectureSet;
	}

}
