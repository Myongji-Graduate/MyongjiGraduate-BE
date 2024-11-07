package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;

public interface FindBasicAcademicalCulturePort {

	Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(String major);

	Set<BasicAcademicalCultureLecture> findDuplicatedLecturesBetweenMajors(User user);
}
