package com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Getter;

@Getter
public class ParsingAnonymousDto {

	private final User anonymous;
	private final TakenLectureInventory takenLectureInventory;

	public ParsingAnonymousDto(User anonymous, TakenLectureInventory takenLectureInventory) {
		this.anonymous = anonymous;
		this.takenLectureInventory = takenLectureInventory;
	}
}
