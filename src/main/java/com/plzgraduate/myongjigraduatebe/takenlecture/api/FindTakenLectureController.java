package com.plzgraduate.myongjigraduatebe.takenlecture.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.response.FindTakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/taken-lectures")
@RequiredArgsConstructor
public class FindTakenLectureController implements FindTakenLectureApiPresentation {

	private final FindTakenLectureUseCase findTakenLectureUseCase;

	@GetMapping
	public FindTakenLectureResponse getTakenLectures(@LoginUser Long userId) {
		TakenLectureInventory takenLectures = findTakenLectureUseCase.findTakenLectures(userId);
		return FindTakenLectureResponse.from(takenLectures);
	}
}
