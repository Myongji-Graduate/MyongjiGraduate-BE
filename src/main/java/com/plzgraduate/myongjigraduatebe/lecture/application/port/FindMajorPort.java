package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;

public interface FindMajorPort {

	Set<MajorLecture> findMajor(String major);
}
