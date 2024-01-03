package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.find;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FindTakenLecture", description = "사용자의 수강과목을 조회하는 API")
public interface FindTakenLectureApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	FindTakenLectureResponse getTakenLectures(@LoginUser Long userId);
}
