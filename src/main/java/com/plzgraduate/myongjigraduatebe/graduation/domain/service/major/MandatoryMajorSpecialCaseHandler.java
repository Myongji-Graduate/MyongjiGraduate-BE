package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public interface MandatoryMajorSpecialCaseHandler {
	boolean isSupport(User user, MajorType majorType);

	MandatorySpecialCaseInformation getMandatorySpecialCaseInformation(
		User user, MajorType majorType, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures);
}
