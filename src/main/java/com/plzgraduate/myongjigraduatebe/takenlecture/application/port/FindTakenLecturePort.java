package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindTakenLecturePort {

	List<TakenLecture> findTakenLecturesByUser(User user);

	Set<TakenLecture> findTakenLectureSetByUser(User user);
}
