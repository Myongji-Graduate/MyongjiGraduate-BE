package com.plzgraduate.myongjigraduatebe.user.application.port.in.check;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StudentNumberDuplicationResponse {

	private final String studentNumber;

	private final boolean notDuplicated;

	@Builder
	private StudentNumberDuplicationResponse(String studentNumber, boolean notDuplicated) {
		this.studentNumber = studentNumber;
		this.notDuplicated = notDuplicated;
	}
}
