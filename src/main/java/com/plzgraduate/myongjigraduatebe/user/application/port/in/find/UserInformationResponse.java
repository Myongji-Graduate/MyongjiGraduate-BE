package com.plzgraduate.myongjigraduatebe.user.application.port.in.find;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInformationResponse {

	private final String studentNumber;

	private final String studentName;

	private final String major;

	@Builder
	private UserInformationResponse(String studentNumber, String studentName, String major) {
		this.studentNumber = studentNumber;
		this.studentName = studentName;
		this.major = major;
	}
}
