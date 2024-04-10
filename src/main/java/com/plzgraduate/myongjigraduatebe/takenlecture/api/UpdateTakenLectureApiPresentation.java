package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.GenerateTakenLectureRequest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "UpdateTakenLecture", description = "수강과목을 수정하는 API")
public interface UpdateTakenLectureApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	void generateTakenLecture(@LoginUser Long userId,
		@Valid @RequestBody GenerateTakenLectureRequest generateTakenLectureRequest);

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@Parameter(name = "takenLectureId", description = "삭제할 수강 과목 ID")
	void deleteTakenLecture(@LoginUser Long userId,
		@Valid @PathVariable("taken-lecture-id") Long takenLectureId);
}
