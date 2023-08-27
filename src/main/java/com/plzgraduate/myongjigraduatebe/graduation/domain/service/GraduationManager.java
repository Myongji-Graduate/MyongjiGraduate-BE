package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public interface GraduationManager<T> {
	DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, Set<T> graduationLectures, int graduationResultTotalCredit);
}
