package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadTakenLecturePort {

	Set<TakenLecture> loadTakenLectures(User user);
}
