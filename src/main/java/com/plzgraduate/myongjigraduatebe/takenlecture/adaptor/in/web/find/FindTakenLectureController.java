package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.find;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
@Tag(name = "FindTakenLecture", description = "사용자의 수강과목을 조회하는 API")
public class FindTakenLectureController {

	private final FindTakenLectureUseCase findTakenLectureUseCase;

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@GetMapping
	public FindTakenLectureResponse getTakenLectures(@LoginUser Long userId) {
		return findTakenLectureUseCase.getTakenLectures(userId);
	}
}
