package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;

public interface MandatoryMajorSpecialCaseHandler {

	boolean isSupport(User user, MajorType majorType);

	MandatorySpecialCaseInformation getMandatorySpecialCaseInformation(
		User user, MajorType majorType, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures);
}
