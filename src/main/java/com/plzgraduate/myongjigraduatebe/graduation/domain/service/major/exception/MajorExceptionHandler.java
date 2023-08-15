package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public interface MajorExceptionHandler {
	boolean isSupport(StudentInformation studentInformation);

	boolean checkMandatoryCondition(StudentInformation studentInformation, Set<TakenLecture> takenLectures,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures);

	int getRemovedMandatoryTotalCredit();
}
