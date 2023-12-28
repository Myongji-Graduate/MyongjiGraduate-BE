package com.plzgraduate.myongjigraduatebe.user.application.port.in.check;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StudentNumberDuplicationResponse {

	@Schema(name = "studentNumber", example = "60202000")
	private final String studentNumber;
	@Schema(name = "notDuplicated", example = "true")
	private final boolean notDuplicated;

	@Builder
	private StudentNumberDuplicationResponse(String studentNumber, boolean notDuplicated) {
		this.studentNumber = studentNumber;
		this.notDuplicated = notDuplicated;
	}
}
