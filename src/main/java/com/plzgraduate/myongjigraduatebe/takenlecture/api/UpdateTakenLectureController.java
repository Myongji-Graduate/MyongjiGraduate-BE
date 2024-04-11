package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.GenerateCustomizedTakenLectureRequest;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.save.GenerateCustomizedTakenLectureUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
public class UpdateTakenLectureController implements UpdateTakenLectureApiPresentation {

	private final GenerateCustomizedTakenLectureUseCase generateCustomizedTakenLectureUseCase;
	private final DeleteTakenLectureUseCase deleteTakenLectureUseCase;

	@PostMapping()
	public void generateCustomizedTakenLecture(@LoginUser Long userId,
		@Valid @RequestBody GenerateCustomizedTakenLectureRequest generateCustomizedTakenLectureRequest) {
		generateCustomizedTakenLectureUseCase.generateCustomizedTakenLecture(userId,
			generateCustomizedTakenLectureRequest.getLectureId());
	}

	@DeleteMapping("{takenLectureId}")
	public void deleteCustomizedTakenLecture(@LoginUser Long userId,
		@Valid @PathVariable Long takenLectureId) {
		deleteTakenLectureUseCase.deleteTakenLecture(userId, takenLectureId);
	}
}
