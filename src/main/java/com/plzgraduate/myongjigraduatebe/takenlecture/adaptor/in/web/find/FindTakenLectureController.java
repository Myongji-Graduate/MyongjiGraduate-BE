package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.find;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureResponse;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
public class FindTakenLectureController {

	private final FindTakenLectureUseCase findTakenLectureUseCase;

	@GetMapping
	public FindTakenLectureResponse getTakenLectures(@LoginUser Long userId) {
		return findTakenLectureUseCase.getTakenLectures(userId);
	}
}
