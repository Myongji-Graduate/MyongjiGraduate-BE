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
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Major;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public class MajorManager implements GraduationManager<Major> {

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		Set<TakenLecture> takenLectures, Set<Major> majorLectures, int graduationResultTotalCredit) {

		removeDuplicateLecture(takenLectures, majorLectures);
		changeMandatoryToElectiveByMajorRange(user, majorLectures);

		Set<Lecture> mandatoryLectures = filterMandatoryLectures(majorLectures);
		Set<Lecture> electiveLectures = filterElectiveLectures(majorLectures);

		List<MajorExceptionHandler> majorExceptionHandlers = List.of(new OptionalMandatoryHandler(), new ReplaceMandatoryMajorHandler());
		MandatoryMajorManager mandatoryMajorManager = new MandatoryMajorManager(majorExceptionHandlers);
		ElectiveMajorManager electiveMajorManager = new ElectiveMajorManager();

		DetailCategoryResult mandantoryDetailCategoryResult = mandatoryMajorManager.createDetailCategoryResult(
			user, takenLectures,
			mandatoryLectures, electiveLectures);

		int electiveMajorTotalCredit = graduationResultTotalCredit - mandantoryDetailCategoryResult.getTotalCredits();
		DetailCategoryResult electiveDetailCategoryResult = electiveMajorManager.createDetailCategoryResult(
			takenLectures, electiveLectures, electiveMajorTotalCredit);

		return DetailGraduationResult.create(MAJOR, graduationResultTotalCredit,
			List.of(mandantoryDetailCategoryResult, electiveDetailCategoryResult));
	}

	private Set<Lecture> filterMandatoryLectures(Set<Major> majorLectures) {
		return majorLectures.stream()
			.filter(major -> major.getIsMandatory() == 1)
			.map(Major::getLecture)
			.collect(Collectors.toSet());
	}

	private Set<Lecture> filterElectiveLectures(Set<Major> majorLectures) {
		return majorLectures.stream()
			.filter(major -> major.getIsMandatory() == 0)
			.map(Major::getLecture)
			.collect(Collectors.toSet());
	}

	private void removeDuplicateLecture(Set<TakenLecture> takenLectures, Set<Major> graduationLectures) {
		Set<Lecture> duplicatedTakenLectures = findDuplicatedTakenLecture(takenLectures);
		graduationLectures.removeIf(graduationLecture ->
			duplicatedTakenLectures.stream()
				.anyMatch(duplicatedTakenLecture ->
					!duplicatedTakenLecture.equals(graduationLecture.getLecture())
						&& duplicatedTakenLecture.getDuplicateCode().equals(graduationLecture.getLecture().getDuplicateCode())
				)
		);
	}

	private Set<Lecture> findDuplicatedTakenLecture(Set<TakenLecture> takenLectures) {
		return takenLectures.stream()
			.map(TakenLecture::getLecture)
			.filter(lecture -> lecture.getDuplicateCode() != null && lecture.getIsRevoked() == 1)
			.collect(Collectors.toSet());
	}

	private void changeMandatoryToElectiveByMajorRange(User user,
		Set<Major> majorsLectures) {
		majorsLectures.forEach(major ->
			major.changeMandatoryToElectiveByEntryYearRange(user.getEntryYear()));
	}

}