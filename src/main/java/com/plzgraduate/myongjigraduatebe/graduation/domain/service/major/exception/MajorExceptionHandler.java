package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface MajorExceptionHandler {
	boolean isSupport(User user);

	boolean checkMandatoryCondition(User user, Set<TakenLecture> takenLectures,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures);

	int getRemovedMandatoryTotalCredit();
}
