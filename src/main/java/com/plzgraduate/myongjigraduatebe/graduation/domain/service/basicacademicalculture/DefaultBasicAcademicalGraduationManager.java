package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.HUMANITIES;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.ICT;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.LAW;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.findBelongingCollege;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class DefaultBasicAcademicalGraduationManager implements BasicAcademicalGraduationManager {

	@Override
	public boolean isSatisfied(String major, int entryYear) {
		return List.of(HUMANITIES, LAW, ICT)
			.contains(findBelongingCollege(major, entryYear));
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory,
		Set<BasicAcademicalCultureLecture> graduationLectures, int basicAcademicalCredit) {

		if(user.getStudentCategory() == StudentCategory.TRANSFER) {
			return DetailGraduationResult.createNonCategorizedGraduationResult(
					basicAcademicalCredit, List.of()
			);
		}
		Set<Lecture> basicAcademicalLectures =
			convertToLectureSet(findCurrentlyRecognizedPolicies(graduationLectures));

		Set<TakenLecture> finishedTakenLecture =
			findRecognizedTakenLectures(graduationLectures, takenLectureInventory);
		Set<Lecture> taken = finishedTakenLecture.stream()
			.map(TakenLecture::getLecture)
			.collect(Collectors.toSet());
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);

		int exchangeCredit = user.getExchangeCredit().getBasicAcademicalCulture();

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			"학문기초교양", true, basicAcademicalCredit);
		removeRecognizedLectures(basicAcademicalLectures, taken);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);
		detailCategoryResult.addTakenCredits(exchangeCredit);

		return DetailGraduationResult.createNonCategorizedGraduationResult(basicAcademicalCredit,
			List.of(detailCategoryResult));
	}

	private Set<BasicAcademicalCultureLecture> findCurrentlyRecognizedPolicies(
		Set<BasicAcademicalCultureLecture> graduationLectures
	) {
		LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
		Semester currentSemester = today.getMonthValue() <= 6
			? Semester.FIRST
			: Semester.SECOND;
		return graduationLectures.stream()
			.filter(policy -> policy.recognizesAt(today.getYear(), currentSemester))
			.collect(Collectors.toSet());
	}
}
