package com.plzgraduate.myongjigraduatebe.lecture.application.port.out;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindMajorPort {

	Set<MajorLecture> findMajor(User user);
}
