package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.UpdateTakenLectureRequest;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.update.UpdateTakenLectureUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
public class UpdateTakenLectureController implements UpdateTakenLectureApiPresentation {
	private final UpdateTakenLectureUseCase updateTakenLectureUseCase;

	@PostMapping("/update")
	public void updateTakenLectures(@LoginUser Long userId, @Valid @RequestBody UpdateTakenLectureRequest updateTakenLectureRequest) {
		updateTakenLectureUseCase.modifyTakenLecture(updateTakenLectureRequest.toCommand(userId));
	}

}
