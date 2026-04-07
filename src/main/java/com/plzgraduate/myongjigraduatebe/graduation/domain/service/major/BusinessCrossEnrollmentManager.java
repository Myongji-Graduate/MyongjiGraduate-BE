package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.UNFITTED_GRADUATION_CATEGORY;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessCrossEnrollmentManager {

	private static final Set<String> ELIGIBLE_BUSINESS_CROSS_ENROLLMENT_MAJORS = Set.of(
		"경영학전공", "국제통상학과", "경영정보학과"
	);

	private final FindMajorPort findMajorPort;

	public boolean supportsSingleMajor(User user) {
		return (user.getStudentCategory() == StudentCategory.NORMAL
			|| user.getStudentCategory() == StudentCategory.CHANGE_MAJOR)
			&& ELIGIBLE_BUSINESS_CROSS_ENROLLMENT_MAJORS.contains(user.getPrimaryMajor());
	}

	public boolean supportsDualMajor(User user) {
		return user.getStudentCategory() == StudentCategory.DUAL_MAJOR
			&& isBusinessCollege(user.getPrimaryMajor(), user.getEntryYear())
			&& isBusinessCollege(user.getDualMajor(), user.getEntryYear());
	}

	public void applySingleMajor(
		User user,
		TakenLectureInventory takenLectureInventory,
		DetailGraduationResult primaryMajorResult
	) {
		validateRequiredArgument(user);
		validateRequiredArgument(takenLectureInventory);
		validateRequiredArgument(primaryMajorResult);
		validateSingleMajorUser(user);

		DetailCategoryResult primaryElective = getPrimaryElectiveCategory(primaryMajorResult);

		Set<String> crossEnrollableLectureIds = ELIGIBLE_BUSINESS_CROSS_ENROLLMENT_MAJORS.stream()
			.filter(major -> !major.equals(user.getPrimaryMajor()))
			.flatMap(major -> findMajorPort.findMajor(major).stream())
			.map(majorLecture -> majorLecture.getLecture().getId())
			.collect(Collectors.toSet());

		int shortfall = Math.max(0, primaryElective.getTotalCredits() - primaryElective.getTakenCredits());
		if (shortfall <= 0) {
			return;
		}

		List<TakenLecture> selectableLectures = takenLectureInventory.getTakenLectures().stream()
			.filter(tl -> crossEnrollableLectureIds.contains(tl.getLecture().getId()))
			.sorted(Comparator.comparing(tl -> tl.getLecture().getId()))
			.toList();

		List<TakenLecture> selectedTakenLectures = selectRecognizableTakenLectures(
			selectableLectures,
			Math.min(shortfall, 9)
		);
		if (selectedTakenLectures.isEmpty()) {
			return;
		}

		List<Lecture> recognizedLectures = selectedTakenLectures.stream()
			.map(TakenLecture::getLecture)
			.toList();
		int addedCredits = primaryElective.addRecognizedLectures(recognizedLectures);
		primaryMajorResult.addCredit(addedCredits);
		takenLectureInventory.handleFinishedTakenLectures(new HashSet<>(selectedTakenLectures));
	}

	public void restorePrimaryMandatoryForDual(
		User user,
		DetailGraduationResult primaryMandatoryResult,
		TakenLectureInventory takenLectureInventory
	) {
		validateRequiredArgument(user);
		validateRequiredArgument(primaryMandatoryResult);
		validateRequiredArgument(takenLectureInventory);
		validateDualMajorUser(user);

		Set<String> candidateIds = findMajorPort.findMajor(user.getDualMajor()).stream()
			.filter(majorLecture -> majorLecture.getIsMandatory() == 1)
			.map(majorLecture -> majorLecture.getLecture().getId())
			.collect(Collectors.toSet());

		Set<TakenLecture> toRestore = getRequiredDetailCategoryByName(
			primaryMandatoryResult,
			PRIMARY_MANDATORY_MAJOR.getName()
		).getTakenLectures().stream()
			.filter(lecture -> candidateIds.contains(lecture.getId()))
			.map(lecture -> TakenLecture.custom(null, lecture))
			.collect(Collectors.toSet());
		takenLectureInventory.restoreTakenLectures(toRestore);
	}

	public void applyDualMajor(
		DetailGraduationResult primaryMandatoryResult,
		DetailGraduationResult primaryElectiveResult,
		DetailGraduationResult dualMandatoryResult,
		DetailGraduationResult dualElectiveResult
	) {
		validateRequiredArgument(primaryMandatoryResult);
		validateRequiredArgument(primaryElectiveResult);
		validateRequiredArgument(dualMandatoryResult);
		validateRequiredArgument(dualElectiveResult);

		DetailCategoryResult primaryElective = getRequiredDetailCategoryByName(
			primaryElectiveResult,
			PRIMARY_ELECTIVE_MAJOR.getName()
		);
		DetailCategoryResult dualElective = getRequiredDetailCategoryByName(
			dualElectiveResult,
			DUAL_ELECTIVE_MAJOR.getName()
		);

		Set<String> primaryExistingIds = collectTakenLectureIds(primaryMandatoryResult, primaryElectiveResult);
		Set<String> dualExistingIds = collectTakenLectureIds(dualMandatoryResult, dualElectiveResult);

		int remainCap = 9;

		int dualShortfall = Math.max(0, dualElective.getTotalCredits() - dualElective.getTakenCredits());
		List<Lecture> lecturesToDual = selectRecognizableLectures(
			primaryElective.getTakenLectures(),
			dualExistingIds,
			Math.min(dualShortfall, remainCap)
		);
		int addedToDual = dualElective.addRecognizedLectures(lecturesToDual);
		dualElectiveResult.addCredit(addedToDual);
		remainCap -= addedToDual;

		if (remainCap <= 0) {
			return;
		}

		int primaryShortfall = Math.max(0, primaryElective.getTotalCredits() - primaryElective.getTakenCredits());
		List<Lecture> lecturesToPrimary = selectRecognizableLectures(
			dualElective.getTakenLectures(),
			primaryExistingIds,
			Math.min(primaryShortfall, remainCap)
		);
		int addedToPrimary = primaryElective.addRecognizedLectures(lecturesToPrimary);
		primaryElectiveResult.addCredit(addedToPrimary);
	}

	private boolean isBusinessCollege(String major, int entryYear) {
		try {
			College college = College.findBelongingCollege(major, entryYear);
			return college == College.BUSINESS || college == College.BUSINESS_NEW;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private DetailCategoryResult getDetailCategoryByName(DetailGraduationResult result, String name) {
		return result.getDetailCategory().stream()
			.filter(r -> r.getDetailCategoryName().equals(name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(UNFITTED_GRADUATION_CATEGORY.toString()));
	}

	private DetailCategoryResult getRequiredDetailCategoryByName(DetailGraduationResult result, String name) {
		return getDetailCategoryByName(result, name);
	}

	private DetailCategoryResult getPrimaryElectiveCategory(DetailGraduationResult result) {
		if (result.getGraduationCategory() == PRIMARY_ELECTIVE_MAJOR) {
			return getRequiredDetailCategoryByName(result, PRIMARY_ELECTIVE_MAJOR.getName());
		}
		return result.getDetailCategory().stream()
			.filter(category -> category.getDetailCategoryName().equals("전공선택"))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(UNFITTED_GRADUATION_CATEGORY.toString()));
	}

	private void validateSingleMajorUser(User user) {
		if (!supportsSingleMajor(user)) {
			throw new IllegalArgumentException(UNFITTED_GRADUATION_CATEGORY.toString());
		}
	}

	private void validateDualMajorUser(User user) {
		if (!supportsDualMajor(user)) {
			throw new IllegalArgumentException(UNFITTED_GRADUATION_CATEGORY.toString());
		}
	}

	private void validateRequiredArgument(Object value) {
		if (Objects.isNull(value)) {
			throw new IllegalArgumentException(UNFITTED_GRADUATION_CATEGORY.toString());
		}
	}

	private Set<String> collectTakenLectureIds(DetailGraduationResult... results) {
		Set<String> lectureIds = new HashSet<>();
		for (DetailGraduationResult result : results) {
			result.getDetailCategory().stream()
				.flatMap(category -> category.getTakenLectures().stream())
				.map(Lecture::getId)
				.forEach(lectureIds::add);
		}
		return lectureIds;
	}

	private List<Lecture> selectRecognizableLectures(
		List<Lecture> candidates,
		Set<String> excludedLectureIds,
		int creditLimit
	) {
		if (creditLimit <= 0) {
			return List.of();
		}

		Set<String> usedLectureIds = new HashSet<>(excludedLectureIds);
		List<Lecture> selectedLectures = new ArrayList<>();
		int selectedCredits = 0;

		for (Lecture lecture : candidates) {
			if (usedLectureIds.contains(lecture.getId())) {
				continue;
			}
			if (selectedCredits + lecture.getCredit() > creditLimit) {
				continue;
			}
			selectedLectures.add(lecture);
			usedLectureIds.add(lecture.getId());
			selectedCredits += lecture.getCredit();
			if (selectedCredits == creditLimit) {
				break;
			}
		}
		return selectedLectures;
	}

	private List<TakenLecture> selectRecognizableTakenLectures(
		List<TakenLecture> candidates,
		int creditLimit
	) {
		if (creditLimit <= 0) {
			return List.of();
		}

		List<TakenLecture> selectedLectures = new ArrayList<>();
		int selectedCredits = 0;
		for (TakenLecture lecture : candidates) {
			if (selectedCredits + lecture.getLecture().getCredit() > creditLimit) {
				continue;
			}
			selectedLectures.add(lecture);
			selectedCredits += lecture.getLecture().getCredit();
			if (selectedCredits == creditLimit) {
				break;
			}
		}
		return selectedLectures;
	}
}
