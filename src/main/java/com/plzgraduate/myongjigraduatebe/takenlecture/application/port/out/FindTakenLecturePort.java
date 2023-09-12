package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindTakenLecturePort {

	List<TakenLecture> findTakenLecturesByUser(User user);
}
