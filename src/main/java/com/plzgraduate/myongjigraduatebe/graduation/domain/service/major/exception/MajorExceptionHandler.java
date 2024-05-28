package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public interface MajorExceptionHandler {
	boolean isSupport(User user, MajorGraduationCategory majorGraduationCategory);

	boolean checkMandatoryCondition(User user, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures);

	int getRemovedMandatoryTotalCredit();
}
