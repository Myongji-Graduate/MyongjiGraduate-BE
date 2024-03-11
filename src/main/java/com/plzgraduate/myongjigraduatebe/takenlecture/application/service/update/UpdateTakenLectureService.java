package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.update;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.FindLecturesByIdUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.update.UpdateTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class UpdateTakenLectureService implements UpdateTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindLecturesByIdUseCase findLecturesByIdUseCase;
	private final DeleteTakenLecturePort deleteTakenLecturePort;
	private final SaveTakenLecturePort saveTakenLecturePort;

	@Override
	public void modifyTakenLecture(Long userId, List<Long> deletedTakenLectureIds, List<Long> addedTakenLectureIds) {
		User user = findUserUseCase.findUserById(userId);
		deleteTakenLecturePort.deleteTakenLecturesByIds(deletedTakenLectureIds);
		List<Lecture> addedLectures = findLecturesByIdUseCase.findLecturesByIds(addedTakenLectureIds);
		addCustomTakenLectures(user, addedLectures);
	}

	private void addCustomTakenLectures(User user, List<Lecture> addedLectures) {
		List<TakenLecture> addedTakenLectures = addedLectures.stream()
			.map(addedLecture -> TakenLecture.custom(user, addedLecture))
			.collect(Collectors.toList());
		saveTakenLecturePort.saveTakenLectures(addedTakenLectures);
	}
}
