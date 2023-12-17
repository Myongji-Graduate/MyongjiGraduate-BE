package com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicInfoResponse {

	private final String name;
	private final String studentNumber;
	private final String major;
	private final int totalCredit;
	private final double takenCredit;

	@Builder
	private BasicInfoResponse(String name, String studentNumber, String major, int totalCredit, double takenCredit) {
		this.name = name;
		this.studentNumber = studentNumber;
		this.major = major;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
	}

	public static BasicInfoResponse of(User user, GraduationResult graduationResult) {
		return BasicInfoResponse.builder()
			.name(user.getName())
			.studentNumber(user.getStudentNumber())
			.major(user.getMajor())
			.totalCredit(graduationResult.getTotalCredit())
			.takenCredit(graduationResult.getTakenCredit()).build();
	}
}
