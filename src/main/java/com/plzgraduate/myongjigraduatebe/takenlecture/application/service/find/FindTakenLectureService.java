package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.find;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindTakenLectureService implements FindTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;

	private final FindTakenLecturePort findTakenLecturePort;

	@Override
	public TakenLectureInventory findTakenLectures(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		List<TakenLecture> takenLectures = findTakenLecturePort.findTakenLecturesByUser(user);
		return TakenLectureInventory.from(new HashSet<>(takenLectures));
	}
}
