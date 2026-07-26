package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculateFusionMajorGraduationService {

	private final FusionMajorMembershipPort membershipPort;

	public List<DetailGraduationResult> calculate(User user, TakenLectureInventory inventory) {
		DetailGraduationResult basic = calculateArea(
			user, inventory, "BASIC",
			GraduationCategory.FUSION_BASIC_ACADEMICAL_CULTURE,
			membershipPort.findRequiredBasicCredit(
				user.getAssociatedMajor(), user.getEntryYear(), user.getPrimaryMajor()));
		DetailGraduationResult major = calculateArea(
			user, inventory, "MAJOR", GraduationCategory.FUSION_MAJOR,
			membershipPort.findRequiredCredit(
				user.getAssociatedMajor(), user.getEntryYear(), user.getPrimaryMajor()));
		return List.of(basic, major);
	}

	private DetailGraduationResult calculateArea(
		User user,
		TakenLectureInventory inventory,
		String area,
		GraduationCategory category,
		int requiredCredit
	) {
		Set<Lecture> curriculum = membershipPort.findLectures(
			user.getAssociatedMajor(), user.getEntryYear(), area);
		Set<String> curriculumIds = curriculum.stream().map(Lecture::getId).collect(Collectors.toSet());
		Map<String, String> equivalenceKeys = membershipPort.findEquivalenceKeys(
			user.getAssociatedMajor(), user.getEntryYear());
		Set<TakenLecture> taken = inventory.getTakenLectures().stream()
			.filter(item -> curriculumIds.contains(item.getLecture().getId()))
			.collect(Collectors.toSet());
		Set<String> recognizedKeys = new java.util.HashSet<>();
		Set<Lecture> takenLectures = taken.stream()
			.sorted(java.util.Comparator.comparing(item -> item.getLecture().getId()))
			.map(TakenLecture::getLecture)
			.filter(lecture -> recognizedKeys.add(
				equivalenceKeys.getOrDefault(lecture.getId(), lecture.getId())))
			.collect(Collectors.toSet());

		Set<String> mandatoryIds = membershipPort.findMandatoryLectureIds(
			user.getAssociatedMajor(), user.getEntryYear(), area);
		Set<String> takenIds = takenLectures.stream()
			.map(Lecture::getId)
			.collect(Collectors.toSet());
		DetailCategoryResult detail = DetailCategoryResult.create(
			user.getAssociatedMajor(), takenIds.containsAll(mandatoryIds), requiredCredit);
		detail.calculate(takenLectures, curriculum);
		inventory.handleFinishedTakenLectures(taken);
		return DetailGraduationResult.create(
			category, requiredCredit, List.of(detail));
	}
}
