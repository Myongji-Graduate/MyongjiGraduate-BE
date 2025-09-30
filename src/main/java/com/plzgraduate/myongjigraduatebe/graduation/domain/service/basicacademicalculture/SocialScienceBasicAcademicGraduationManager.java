package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.SOCIAL_SCIENCE;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.findBelongingCollege;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class SocialScienceBasicAcademicGraduationManager implements
	BasicAcademicalGraduationManager {

	private static final int TWENTY_THREE_YEAR = 2023;
	private static final Set<Lecture> lecturesAcceptTakenAfter2023 = Set.of(
		Lecture.of("KMD02102", "국제정치의이해", 3, 0, null),
		Lecture.of("KMD02108", "현대사회와정보", 3, 0, null),
		Lecture.of("KMD02140", "경제학들어가기", 3, 0, null),
		Lecture.of("KMD02186", "직무커뮤니케이션능력개발", 3, 0, null),
		Lecture.of("KMD02113", "인간관계와커뮤니케이션", 3, 0, null),
		Lecture.of("KMD02104", "인터넷과커뮤니케이션", 3, 0, null),
		Lecture.of("KMD02114", "미시경제학원론", 3, 0, null),
		Lecture.of("KMD02115", "거시경제학원론", 3, 0, null),
		Lecture.of("KMB02163", "배려의행복학", 3, 0, null)
	);

	@Override
	public boolean isSatisfied(String major, int entryYear) {
		return findBelongingCollege(major, entryYear) == SOCIAL_SCIENCE;
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
		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> basicAcademicalLectures.contains(takenLecture.getLecture()))
			.filter(takenLecture -> {
				Lecture lecture = takenLecture.getLecture();
				if (lecturesAcceptTakenAfter2023.contains(lecture)) {
					return takenLecture.takenAfter(TWENTY_THREE_YEAR);
				}
				return true;
			})
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);

		int exchangeCredit = user.getExchangeCredit().getBasicAcademicalCulture();
		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			"학문기초교양", true, basicAcademicalCredit);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);
		detailCategoryResult.addTakenCredits(exchangeCredit);

		return DetailGraduationResult.createNonCategorizedGraduationResult(basicAcademicalCredit,
			List.of(detailCategoryResult));
	}
}
