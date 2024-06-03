package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindBasicAcademicalCulturePort {

	Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(String major);

	Set<BasicAcademicalCultureLecture> findDuplicatedLecturesBetweenMajors(User user);
}
