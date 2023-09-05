package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.GetTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.GetTakenLectureResponse;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
public class GetTakenLectureController {

	private final GetTakenLecturePort getTakenLecturePort;

	@GetMapping
	public GetTakenLectureResponse getTakenLectures(@LoginUser Long userId) {
		return getTakenLecturePort.getTakenLectures(userId);
	}
}
