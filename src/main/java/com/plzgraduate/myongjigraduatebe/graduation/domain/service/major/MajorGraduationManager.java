package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MajorGraduationManager {

	private final MandatoryMajorManager mandatoryMajorManager;
	private final ElectiveMajorManager electiveMajorManager;

	/**
	 *
	 * @param user 사용자
	 * @param majorType 주전공, 복수전공, 부전공
	 * @param takenLectureInventory 수강과목 목록
	 * @param majorLectures 해당 사용자의 전공과목
	 * @param graduationResultTotalCredit 해당 사용자의 전공 졸업 학점
	 * @return 전공 카테고리에 대한 졸업 결과 반환
	 */
	public DetailGraduationResult createDetailGraduationResult(User user, MajorType majorType,
		TakenLectureInventory takenLectureInventory, Set<MajorLecture> majorLectures, int graduationResultTotalCredit) {

		removeDuplicateLectureIfTaken(takenLectureInventory, majorLectures);
		changeMandatoryToElectiveByMajorRange(user, majorLectures);

		Set<Lecture> mandatoryLectures = filterMandatoryLectures(majorLectures);
		Set<Lecture> electiveLectures = filterElectiveLectures(majorLectures);

		DetailCategoryResult mandantoryDetailCategoryResult = mandatoryMajorManager.createDetailCategoryResult(
			user, takenLectureInventory, mandatoryLectures, electiveLectures, majorType);

		int electiveMajorTotalCredit = graduationResultTotalCredit - mandantoryDetailCategoryResult.getTotalCredits();
		DetailCategoryResult electiveDetailCategoryResult = electiveMajorManager.createDetailCategoryResult(
			takenLectureInventory, electiveLectures, electiveMajorTotalCredit);

		return DetailGraduationResult.createNonCategorizedGraduationResult(graduationResultTotalCredit,
			List.of(mandantoryDetailCategoryResult, electiveDetailCategoryResult));
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
	 * 전공과목에서 사용자의 수강과목 중 중복과목들을 삭제한다.
	 * ex) A(폐지) -> B(폐지) -> C(진행) 인 과목이 있다고하면 A,B,C의 과목중복코드는 같다. 사용자가 B과목을 들었다면 A,C는 전공과목에서 삭제한다.
	 * 사용자가 B과목을 들었다면 A,C는 전공과목에서 삭제한다.
	 * B과목만 takenLectures(수강했던 전공과목)에 넣어주면 되고 A,C 과목은 haveToTLectures(들어야하는 전공과목)에 넣어주면 안되기 떄문이다.
	 */
	private void removeDuplicateLectureIfTaken(TakenLectureInventory takenLectureInventory,
		Set<MajorLecture> graduationLectures) {
		Set<Lecture> duplicatedTakenLectures = findDuplicatedTakenLecture(takenLectureInventory);
		graduationLectures.removeIf(graduationLecture ->
			duplicatedTakenLectures.stream()
				.anyMatch(duplicatedTakenLecture ->
					!duplicatedTakenLecture.equals(graduationLecture.getLecture())
						&& duplicatedTakenLecture.getDuplicateCode()
						.equals(graduationLecture.getLecture().getDuplicateCode())
				)
		);
	}

	/**
	 * 사용자의 수강과목에서 중복코드가 있고 폐지된 과목들을 반환한다.
	 * 중복코드가 있고 폐지된 과목일 경우 이미 해당 과목을 대체하는 과목이 있다는 뜻이기 때문.
	 */
	private Set<Lecture> findDuplicatedTakenLecture(TakenLectureInventory takenLectureInventory) {
		return takenLectureInventory.getTakenLectures().stream()
			.map(TakenLecture::getLecture)
			.filter(lecture -> lecture.getDuplicateCode() != null && lecture.getIsRevoked() == 1)
			.collect(Collectors.toSet());
	}

	private void changeMandatoryToElectiveByMajorRange(User user,
		Set<MajorLecture> majorsLectures) {
		majorsLectures.forEach(major ->
			major.changeMandatoryToElectiveByEntryYearRange(user.getEntryYear()));
	}

}
