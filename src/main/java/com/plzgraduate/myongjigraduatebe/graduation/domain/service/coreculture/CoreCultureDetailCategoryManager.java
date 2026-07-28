package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import static com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester.FIRST;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MandatoryOptionResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CoreCultureDetailCategoryManager {

	private static final String 과학과기술_예외_과목_인정코드 = "KMA02136";
	private static final Set<String> 문화와예술_예외_과목_인정코드 = Set.of(
		"KMA02155",
		"KMA02156"
	);

	public DetailCategoryResult generate(
		User user, TakenLectureInventory takenLectureInventory,
		Set<CoreCulture> graduationLectures, CoreCultureCategory category
	) {
		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			return DetailCategoryResult.create(category.getName(), true, 0);
		}

		Set<Lecture> graduationCoreCultureLectures = categorizeCoreCultures(
			graduationLectures,
			category
		);
		Set<Lecture> displayCoreCultureLectures = new HashSet<>(graduationCoreCultureLectures);
		removeIctCoreCultureExceptionFromDisplay(user, displayCoreCultureLectures);
		List<Lecture> optionCandidates = displayCoreCultureLectures.stream()
			.filter(lecture -> lecture.getIsRevoked() == 0)
			.sorted(Comparator.comparing(Lecture::getId))
			.toList();
		Set<String> graduationRecognitionCodes = graduationCoreCultureLectures.stream()
			.map(Lecture::getRecognitionCode)
			.collect(Collectors.toSet());
		List<TakenLecture> matchedTakenLectures = takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> graduationRecognitionCodes.contains(
				takenLecture.getLecture().getRecognitionCode()))
			.sorted(Comparator
				.comparing(TakenLecture::getYear,
					Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(TakenLecture::getSemester,
					Comparator.nullsLast(Comparator.reverseOrder()))
				.thenComparing(takenLecture -> takenLecture.getLecture().getId(),
					Comparator.reverseOrder()))
			.collect(Collectors.toList());
		Map<String, TakenLecture> latestTakenLectureByRecognitionCode = new LinkedHashMap<>();
		matchedTakenLectures.forEach(
			takenLecture -> latestTakenLectureByRecognitionCode.putIfAbsent(
				takenLecture.getLecture().getRecognitionCode(), takenLecture));
		Set<TakenLecture> recognizedTakenLectures = new HashSet<>(
			latestTakenLectureByRecognitionCode.values());
		Set<Lecture> taken = recognizedTakenLectures.stream()
			.map(TakenLecture::getLecture)
			.collect(Collectors.toSet());
		takenLectureInventory.handleFinishedTakenLectures(new HashSet<>(matchedTakenLectures));

		DetailCategoryResult commonCultureDetailCategoryResult = DetailCategoryResult.create(
			category.getName(), true, category.getTotalCredit());
		calculateFreeElectiveLeftCredit(user, taken, commonCultureDetailCategoryResult);
		calculateNormalLeftCredit(taken, recognizedTakenLectures,
			commonCultureDetailCategoryResult);
		displayCoreCultureLectures.removeIf(lecture ->
			latestTakenLectureByRecognitionCode.containsKey(lecture.getRecognitionCode()));
		commonCultureDetailCategoryResult.calculate(taken, displayCoreCultureLectures);
		commonCultureDetailCategoryResult.addMandatoryOptions(List.of(
			new MandatoryOptionResult(
				category.getName() + " 선택",
				1,
				taken.isEmpty() ? 0 : 1,
				optionCandidates
			)
		));

		return commonCultureDetailCategoryResult;
	}

	private void removeIctCoreCultureExceptionFromDisplay(
		User user, Set<Lecture> displayCoreCultureLectures
	) {
		if (!isIctCollege(user)) {
			return;
		}
		displayCoreCultureLectures.removeIf(lecture ->
			lecture.getRecognitionCode().equals(과학과기술_예외_과목_인정코드));
	}

	private Set<Lecture> categorizeCoreCultures(
		Set<CoreCulture> graduationLectures,
		CoreCultureCategory category
	) {
		return graduationLectures.stream()
			.filter(coreCulture -> coreCulture.getCoreCultureCategory() == category)
			.map(CoreCulture::getLecture)
			.collect(Collectors.toSet());
	}

	private void calculateFreeElectiveLeftCredit(
		User user, Set<Lecture> taken,
		DetailCategoryResult commonCultureDetailCategoryResult
	) {
		if (isIctCollege(user) && taken.removeIf(
			lecture -> lecture.getRecognitionCode().equals(과학과기술_예외_과목_인정코드))) {
			int exceptionLectureCredit = 3;
			commonCultureDetailCategoryResult.addFreeElectiveLeftCredit(exceptionLectureCredit);
		}
	}

	private boolean isIctCollege(User user) {
		try {
			College college = College.findBelongingCollege(
				user.getPrimaryMajor(), user.getEntryYear());
			return college == College.ICT || college == College.ARTIFICIAL_INTELLIGENCE_SOFTWARE;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	private void calculateNormalLeftCredit(
		Set<Lecture> taken,
		Set<TakenLecture> finishedTakenLecture,
		DetailCategoryResult commonCultureDetailCategoryResult
	) {
		List<TakenLecture> cultureAndArtExceptionLectures = finishedTakenLecture.stream()
			.filter(takenLecture -> 문화와예술_예외_과목_인정코드.contains(
				takenLecture.getLecture().getRecognitionCode())
				&& takenLecture.getYear() == 2022
				&& takenLecture.getSemester() == FIRST)
			.collect(Collectors.toList());
		if (!cultureAndArtExceptionLectures.isEmpty()) {
			cultureAndArtExceptionLectures.stream()
				.map(TakenLecture::getLecture)
				.forEach(taken::remove);
			int normalLeftCredit = cultureAndArtExceptionLectures.stream()
				.mapToInt(exceptionLecture -> exceptionLecture.getLecture()
					.getCredit())
				.sum();
			commonCultureDetailCategoryResult.addNormalLeftCredit(normalLeftCredit);
		}
	}
}
