package com.plzgraduate.myongjigraduatebe.takenlecture.application.service.delete;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
public class DeleteTakenLectureServiceById implements DeleteTakenLectureUseCase {

	private final DeleteTakenLecturePort deleteTakenLecturePort;

	@Override
	public void deleteTakenLecture(Long deletedTakenLectureId) {
		deleteTakenLecturePort.deleteTakenLectureById(deletedTakenLectureId);
	}
}
