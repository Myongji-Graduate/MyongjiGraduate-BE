package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.update;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.update.UpdateTakenLectureUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
@Tag(name = "UpdateTakenLecture", description = "수강과목을 수정하는 API")
public class UpdateTakenLectureController {
	private final UpdateTakenLectureUseCase updateTakenLectureUseCase;

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@PostMapping("/update")
	public void updateTakenLectures(@LoginUser Long userId, @Valid @RequestBody UpdateTakenLectureRequest updateTakenLectureRequest) {
		updateTakenLectureUseCase.updateTakenLecture(updateTakenLectureRequest.toCommand(userId));
	}

}
