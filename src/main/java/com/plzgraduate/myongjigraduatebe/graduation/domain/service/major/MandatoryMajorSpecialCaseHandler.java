package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public interface MandatoryMajorSpecialCaseHandler {
	boolean isSupport(User user, MajorGraduationCategory majorGraduationCategory);

	MandatorySpecialCaseInformation getMandatorySpecialCaseInformation(
		User user, MajorGraduationCategory majorGraduationCategory, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures);
}
