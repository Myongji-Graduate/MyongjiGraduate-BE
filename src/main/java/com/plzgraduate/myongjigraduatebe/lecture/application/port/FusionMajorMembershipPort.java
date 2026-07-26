package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FusionMajorMembershipPort {

	Set<String> findFusionMajorLectureIds(List<String> lectureIds);

	Set<Lecture> findLectures(String fusionMajor, int entryYear);

	Set<Lecture> findLectures(String fusionMajor, int entryYear, String requirementArea);

	Set<String> findMandatoryLectureIds(
		String fusionMajor, int entryYear, String requirementArea);

	Map<String, String> findEquivalenceKeys(String fusionMajor, int entryYear);

	int findRequiredCredit(String fusionMajor, int entryYear);

	default int findRequiredCredit(
		String fusionMajor, int entryYear, String primaryMajor
	) {
		return findRequiredCredit(fusionMajor, entryYear);
	}

	int findRequiredBasicCredit(String fusionMajor, int entryYear, String primaryMajor);

	int findRequiredPrimaryMajorCredit(String fusionMajor, int entryYear, String primaryMajor);

	default boolean canTake(String lectureId, String fusionMajor, int entryYear) {
		return findLectures(fusionMajor, entryYear).stream()
			.anyMatch(lecture -> lecture.getId().equals(lectureId));
	}
}
