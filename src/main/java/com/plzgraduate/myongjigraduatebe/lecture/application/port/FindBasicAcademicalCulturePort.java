package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;

public interface FindBasicAcademicalCulturePort {

	Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(String major);
}
