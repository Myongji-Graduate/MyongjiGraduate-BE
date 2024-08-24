package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicInfoResponse {

	@Schema(name = "name", example = "홍길동")
	private final String name;
	@Schema(name = "studentNumber", example = "60202000")
	private final String studentNumber;
	private final MajorInfoResponse major;
	@Schema(name = "totalCredit", example = "132")
	private final int totalCredit;
	@Schema(name = "takenCredit", example = "50")
	private final double takenCredit;

	@Builder
	private BasicInfoResponse(String name, String studentNumber, MajorInfoResponse major, int totalCredit, double takenCredit) {
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
			.major(MajorInfoResponse.from(user))
			.totalCredit(graduationResult.getTotalCredit())
			.takenCredit(graduationResult.getTakenCredit()).build();
	}
}
