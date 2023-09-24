package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.MAJOR;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception.MajorExceptionHandler;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception.OptionalMandatoryHandler;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception.ReplaceMandatoryMajorHandler;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public class MajorManager implements GraduationManager<MajorLecture> {

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, Set<MajorLecture> majorLectureLectures, int graduationResultTotalCredit) {
		removeDuplicateLecture(takenLectureInventory, majorLectureLectures);
		changeMandatoryToElectiveByMajorRange(user, majorLectureLectures);

		Set<Lecture> mandatoryLectures = filterMandatoryLectures(majorLectureLectures);
		Set<Lecture> electiveLectures = filterElectiveLectures(majorLectureLectures);

		List<MajorExceptionHandler> majorExceptionHandlers = List.of(new OptionalMandatoryHandler(),
			new ReplaceMandatoryMajorHandler());
		MandatoryMajorManager mandatoryMajorManager = new MandatoryMajorManager(majorExceptionHandlers);
		ElectiveMajorManager electiveMajorManager = new ElectiveMajorManager();

		DetailCategoryResult mandantoryDetailCategoryResult = mandatoryMajorManager.createDetailCategoryResult(
			user, takenLectureInventory, mandatoryLectures, electiveLectures);

		int electiveMajorTotalCredit = graduationResultTotalCredit - mandantoryDetailCategoryResult.getTotalCredits();
		DetailCategoryResult electiveDetailCategoryResult = electiveMajorManager.createDetailCategoryResult(
			takenLectureInventory, electiveLectures, electiveMajorTotalCredit);

		return DetailGraduationResult.create(MAJOR, graduationResultTotalCredit,
			List.of(mandantoryDetailCategoryResult, electiveDetailCategoryResult));
	}

	private Set<Lecture> filterMandatoryLectures(Set<MajorLecture> majorLectureLectures) {
		return majorLectureLectures.stream()
			.filter(major -> major.getIsMandatory() == 1)
			.map(MajorLecture::getLecture)
			.collect(Collectors.toSet());
	}

	private Set<Lecture> filterElectiveLectures(Set<MajorLecture> majorLectureLectures) {
		return majorLectureLectures.stream()
			.filter(major -> major.getIsMandatory() == 0)
			.map(MajorLecture::getLecture)
			.collect(Collectors.toSet());
	}

	private void removeDuplicateLecture(TakenLectureInventory takenLectureInventory, Set<MajorLecture> graduationLectures) {
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
