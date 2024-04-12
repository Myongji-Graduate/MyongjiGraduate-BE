package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.save;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.GenerateCustomizedTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
public class GenerateCustomizedTakenLectureService implements GenerateCustomizedTakenLectureUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindLecturePort findLecturePort;
	private final SaveTakenLecturePort saveTakenLecturePort;
	private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

	@Override
	public void generateCustomizedTakenLecture(final Long userId, final Long addedTakenLectureId) {
		User user = findUserUseCase.findUserById(userId);
		Lecture lecture = findLecturePort.findLectureById(addedTakenLectureId);
		addCustomTakenLecture(user, lecture);
		generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user);
	}

	private void addCustomTakenLecture(User user, Lecture addedLecture) {
		TakenLecture addedTakenLecture = TakenLecture.custom(user, addedLecture);
		saveTakenLecturePort.saveTakenLecture(addedTakenLecture);
	}
}
