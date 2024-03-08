package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.UpdateTakenLectureRequest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UpdateTakenLecture", description = "수강과목을 수정하는 API")
public interface UpdateTakenLectureApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	void updateTakenLectures(@LoginUser Long userId, @Valid @RequestBody UpdateTakenLectureRequest updateTakenLectureRequest);
}
