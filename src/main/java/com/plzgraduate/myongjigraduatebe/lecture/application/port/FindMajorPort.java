package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import java.util.Set;

public interface FindMajorPort {

	Set<MajorLecture> findMajor(String major);
}
