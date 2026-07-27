package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MajorGraduationManager {

	private static final int 경영학과_외국인학생_전공필수_학번 = 22;
	private static final List<String> 국제학생을위한경영학개론_과목코드 = Arrays.asList("HCA02507", "HCA02508");
	private static final int 프로그래밍_교과_중복_시작년도 = 2026;
	private static final Set<Set<String>> 프로그래밍_교과_경과조치_조합 = Set.of(
		Set.of("HEB01101", "HEB01102"),
		Set.of("HEB01101", "HEF01101"),
		Set.of("HEB01103", "HEB01105"),
		Set.of("HEB01103", "HEF01102")
	);

	private final MandatoryMajorManager mandatoryMajorManager;
	private final ElectiveMajorManager electiveMajorManager;

	/**
	 * @param user                        사용자
	 * @param majorType                   주전공, 복수전공, 부전공
	 * @param takenLectureInventory       수강과목 목록
	 * @param majorLectures               해당 사용자의 전공과목
	 * @param graduationResultTotalCredit 해당 사용자의 전공 졸업 학점
	 * @return 전공 카테고리에 대한 졸업 결과 반환
	 */
	public DetailGraduationResult createDetailGraduationResult(
		User user, MajorType majorType,
		TakenLectureInventory takenLectureInventory, Set<MajorLecture> majorLectures,
		int graduationResultTotalCredit,
		List<OptionalMandatoryPolicy> optionalMandatoryPolicies
	) {

		majorLectures.removeIf(major -> !major.isApplicableByEntryYear(user.getEntryYear())
			|| (major.getLecture().getIsRevoked() == 1 && major.getIsMandatory() == 1));
		replaceRevokedLectureWithActivePolicyIfTaken(takenLectureInventory, majorLectures);
		changeMandatoryToElectiveByMajorRange(user, majorLectures);

		Set<Lecture> mandatoryLectures = filterMandatoryLectures(majorLectures);
		Set<Lecture> electiveLectures = filterElectiveLectures(majorLectures);

		if (user.isForeignerStudent()
			&& user.isAnyMajorMatched("경영학과")
			&& user.checkAfterEntryYear(경영학과_외국인학생_전공필수_학번)) {
			Set<Lecture> 국제학생을위한경영학개론 = electiveLectures.stream()
				.filter(lecture -> 국제학생을위한경영학개론_과목코드.contains(lecture.getId()))
				.collect(Collectors.toSet());
			mandatoryLectures.addAll(국제학생을위한경영학개론);
			electiveLectures.removeAll(국제학생을위한경영학개론);
		}

		DetailCategoryResult mandantoryDetailCategoryResult = mandatoryMajorManager.createDetailCategoryResult(
			user, takenLectureInventory, mandatoryLectures, electiveLectures, majorType,
			optionalMandatoryPolicies);

		int electiveMajorTotalCredit =
			graduationResultTotalCredit - mandantoryDetailCategoryResult.getTotalCredits();
		DetailCategoryResult electiveDetailCategoryResult = electiveMajorManager.createDetailCategoryResult(
			takenLectureInventory, electiveLectures, electiveMajorTotalCredit, user);

		return DetailGraduationResult.createNonCategorizedGraduationResult(
			graduationResultTotalCredit,
			List.of(mandantoryDetailCategoryResult, electiveDetailCategoryResult)
		);
	}

	/**
	 * 전공과목에서 필수 과목 Set을 반환한다.
	 */
	private Set<Lecture> filterMandatoryLectures(Set<MajorLecture> majorLectures) {
		return majorLectures.stream()
			.filter(major -> major.getIsMandatory() == 1)
			.map(MajorLecture::getLecture)
			.collect(Collectors.toSet());
	}

	/**
	 * 전공과목에서 선택 과목 Set을 반환한다.
	 */
	private Set<Lecture> filterElectiveLectures(Set<MajorLecture> majorLectures) {
		return majorLectures.stream()
			.filter(major -> major.getIsMandatory() == 0)
			.map(MajorLecture::getLecture)
			.collect(Collectors.toSet());
	}

	/**
	 * 폐강 과목은 전공 정책의 후보가 아니다. 다만 실제 수강 이력이 있고 같은 중복인정 코드의
	 * 활성 과목이 해당 학과·학번 전공 정책에 있으면, 활성 과목 정책을 수강한 폐강 과목에 적용한다.
	 * 폐강 전필 행은 계산 후보에서 제외하지만, 폐강 전선 이력은 기존 전공선택 학점으로 유지한다.
	 */
	private void replaceRevokedLectureWithActivePolicyIfTaken(
		TakenLectureInventory takenLectureInventory,
		Set<MajorLecture> graduationLectures
	) {
		Set<Lecture> duplicatedTakenLectures = findDuplicatedTakenLecture(takenLectureInventory);
		for (Lecture duplicatedTakenLecture : duplicatedTakenLectures) {
			Set<MajorLecture> activeReplacementPolicies = graduationLectures.stream()
				.filter(graduationLecture -> graduationLecture.getLecture().getIsRevoked() == 0)
				.filter(graduationLecture -> duplicatedTakenLecture.getDuplicateCode()
					.equals(graduationLecture.getLecture().getDuplicateCode()))
				.collect(Collectors.toSet());

			if (activeReplacementPolicies.isEmpty()) {
				continue;
			}

			for (MajorLecture policy : activeReplacementPolicies) {
				if (!isProgrammingCourseRecognizedSeparately(
					takenLectureInventory,
					duplicatedTakenLecture,
					policy.getLecture())) {
					graduationLectures.remove(policy);
				}
				graduationLectures.add(MajorLecture.of(
					duplicatedTakenLecture,
					policy.getMajor(),
					policy.getIsMandatory(),
					policy.getAppliedStartEntryYear(),
					policy.getAppliedEndEntryYear()));
			}
		}
	}

	private boolean isProgrammingCourseRecognizedSeparately(
		TakenLectureInventory takenLectureInventory,
		Lecture firstLecture,
		Lecture secondLecture
	) {
		if (!프로그래밍_교과_경과조치_조합.contains(
			Set.of(firstLecture.getId(), secondLecture.getId()))) {
			return false;
		}
		return wasTakenBeforeProgrammingDuplicatePolicy(takenLectureInventory, firstLecture)
			&& wasTakenBeforeProgrammingDuplicatePolicy(takenLectureInventory, secondLecture);
	}

	private boolean wasTakenBeforeProgrammingDuplicatePolicy(
		TakenLectureInventory takenLectureInventory,
		Lecture lecture
	) {
		return takenLectureInventory.getTakenLectures()
			.stream()
			.anyMatch(takenLecture -> takenLecture.getLecture().equals(lecture)
				&& takenLecture.getYear() != null
				&& takenLecture.getYear() < 프로그래밍_교과_중복_시작년도);
	}

	/**
	 * 사용자의 수강과목에서 중복코드가 있고 폐지된 과목들을 반환한다. 중복코드가 있고 폐지된 과목일 경우 이미 해당 과목을 대체하는 과목이 있다는 뜻이기 때문.
	 */
	private Set<Lecture> findDuplicatedTakenLecture(TakenLectureInventory takenLectureInventory) {
		return takenLectureInventory.getTakenLectures()
			.stream()
			.map(TakenLecture::getLecture)
			.filter(lecture -> lecture.getDuplicateCode() != null && lecture.getIsRevoked() == 1)
			.collect(Collectors.toSet());
	}

	private void changeMandatoryToElectiveByMajorRange(
		User user,
		Set<MajorLecture> majorsLectures
	) {
		majorsLectures.forEach(major ->
			major.changeMandatoryToElectiveByEntryYearRange(user.getEntryYear()));
	}

}
