package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public interface GraduationManager<T> {
	DetailGraduationResult createDetailGraduationResult(StudentInformation studentInformation,
		TakenLectureInventory takenLectureInventory, Set<T> graduationLectures, int graduationResultTotalCredit);
}
