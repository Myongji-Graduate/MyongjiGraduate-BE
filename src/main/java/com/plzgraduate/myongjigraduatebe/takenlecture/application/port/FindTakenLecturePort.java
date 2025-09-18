package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FindTakenLecturePort {

	List<TakenLecture> findTakenLecturesByUser(User user);

	Set<TakenLecture> findTakenLectureSetByUser(User user);

	List<String> findTakenLectureIdsByUserAndCodes(Long userId, Collection<String> codes);
}
