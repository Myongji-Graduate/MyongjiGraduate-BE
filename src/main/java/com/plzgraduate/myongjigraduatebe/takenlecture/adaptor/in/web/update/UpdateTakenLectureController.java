package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.update;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.update.UpdateTakenLectureUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
class UpdateTakenLectureController {
	private final UpdateTakenLectureUseCase updateTakenLectureUseCase;

	@PostMapping("/update")
	public void updateTakenLectures(@LoginUser Long userId, @Valid @RequestBody UpdateTakenLectureRequest updateTakenLectureRequest) {
		updateTakenLectureUseCase.updateTakenLecture(updateTakenLectureRequest.toCommand(userId));
	}

}
