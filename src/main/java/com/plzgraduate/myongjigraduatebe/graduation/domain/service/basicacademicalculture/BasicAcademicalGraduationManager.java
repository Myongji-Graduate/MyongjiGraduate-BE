package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface BasicAcademicalGraduationManager extends
	GraduationManager<BasicAcademicalCultureLecture> {

	boolean isSatisfied(String major, int entryYear);

	default Set<Lecture> convertToLectureSet(
		Set<BasicAcademicalCultureLecture> basicAcademicalCultureLectures) {
		return basicAcademicalCultureLectures.stream()
			.map(BasicAcademicalCultureLecture::getLecture)
			.collect(Collectors.toSet());
	}

	default void removeRecognizedLectures(Set<Lecture> graduationLectures, Set<Lecture> takenLectures) {
		Set<String> takenRecognitionCodes = takenLectures.stream()
			.map(Lecture::getRecognitionCode)
			.collect(Collectors.toSet());
		graduationLectures.removeIf(lecture ->
			takenRecognitionCodes.contains(lecture.getRecognitionCode()));
	}

	default Set<TakenLecture> findRecognizedTakenLectures(
		Set<BasicAcademicalCultureLecture> policies,
		TakenLectureInventory takenLectureInventory
	) {
		Set<String> recognizedCodes = new HashSet<>();
		return takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> policies.stream()
				.anyMatch(policy -> policy.recognizes(takenLecture)))
			.filter(takenLecture -> recognizedCodes.add(
				takenLecture.getLecture().getRecognitionCode()))
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

}
