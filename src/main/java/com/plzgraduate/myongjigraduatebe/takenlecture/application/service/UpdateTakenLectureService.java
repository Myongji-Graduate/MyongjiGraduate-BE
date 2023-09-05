package com.plzgraduate.myongjigraduatebe.takenlecture.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.UpdateTakenLectureCommand;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.UpdateTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class UpdateTakenLectureService implements UpdateTakenLecturePort{

	private final LoadUserPort loadUserPort;
	private final DeleteTakenLecturePort deleteTakenLecturePort;
	private final LoadLecturePort loadLecturePort;
	private final SaveTakenLecturePort saveTakenLecturePort;
	@Override
	public void updateTakenLecture(UpdateTakenLectureCommand updateTakenLectureCommand) {
		Long userId = updateTakenLectureCommand.getUserId();
		User user = loadUserPort.loadUserById(userId);
		deleteTakenLectures(updateTakenLectureCommand);
		List<Lecture> addedLectures = findAddedLecturesByIds(
			updateTakenLectureCommand);
		addCustomTakenLectures(user, addedLectures);
	}

	private void addCustomTakenLectures(User user, List<Lecture> addedLectures) {
		List<TakenLecture> addedTakenLectures = addedLectures.stream()
			.map(addedLecture -> TakenLecture.custom(user, addedLecture))
			.collect(Collectors.toList());
		saveTakenLecturePort.saveTakenLectures(addedTakenLectures);
	}

	private List<Lecture> findAddedLecturesByIds(UpdateTakenLectureCommand updateTakenLectureCommand) {
		List<Long> addedLectureIds = updateTakenLectureCommand.getAddedTakenLectures();
		List<Lecture> addedLectures = loadLecturePort.loadLecturesByIds(addedLectureIds);
		return addedLectures;
	}

	private void deleteTakenLectures(UpdateTakenLectureCommand updateTakenLectureCommand) {
		List<Long> deletedTakenLectureIds = updateTakenLectureCommand.getDeletedTakenLectures();
		deleteTakenLecturePort.deleteTakenLecturesByIds(deletedTakenLectureIds);
	}

}
