package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.BUSINESS;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.findBelongingCollege;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BusinessBasicAcademicalGraduationManager implements BasicAcademicalGraduationManager {

	@Override
	public boolean isSatisfied(String major, int entryYear) {
		return findBelongingCollege(major, entryYear) == BUSINESS;
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
															   TakenLectureInventory takenLectureInventory,
															   Set<BasicAcademicalCultureLecture> graduationLectures,
															   int basicAcademicalCredit) {

		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			return DetailGraduationResult.createNonCategorizedGraduationResult(
					basicAcademicalCredit, List.of()
			);
		}

		Set<Lecture> basicAcademicalLectures = convertToLectureSet(graduationLectures);

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

}
