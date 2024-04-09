package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.GenerateTakenLectureRequest;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.GenerateTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
public class UpdateTakenLectureController implements UpdateTakenLectureApiPresentation {

	private final GenerateTakenLectureUseCase generateTakenLectureUseCase;
	private final DeleteTakenLectureUseCase deleteTakenLectureUseCase;

	@PostMapping()
	public void generateTakenLecture(@LoginUser Long userId,
		@Valid @RequestBody GenerateTakenLectureRequest generateTakenLectureRequest) {
		generateTakenLectureUseCase.generateTakenLecture(userId, generateTakenLectureRequest.getLectureId());
	}

	@DeleteMapping("{taken-lecture-id}")
	public void deleteTakenLecture(@Valid @PathVariable("taken-lecture-id") Long takenLectureId) {
		deleteTakenLectureUseCase.deleteTakenLecture(takenLectureId);
	}
}
