package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;

public interface GraduationManager<T> {

	DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, Set<T> graduationLectures,
		int graduationResultTotalCredit);
}
