package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInformationResponse {

	@Schema(name = "studentNumber", example = "60202000")
	private final String studentNumber;
	@Schema(name = "studentName", example = "홍길동")
	private final String studentName;
	@Schema(name = "completeDivision")
	private final List<CompleteDivisionResponse> completeDivision = new ArrayList<>(3);
	private final int totalCredit;
	private final double takenCredit;
	private final boolean graduated;

	@Builder
	private UserInformationResponse(String studentNumber, String studentName, int totalCredit,
		double takenCredit,
		boolean graduated) {
		this.studentNumber = studentNumber;
		this.studentName = studentName;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduated;
	}

	public static UserInformationResponse from(User user) {
		UserInformationResponse userInformation = UserInformationResponse.builder()
			.studentNumber(user.getStudentNumber())
			.studentName(user.getName())
			.totalCredit(user.getTotalCredit())
			.takenCredit(user.getTakenCredit())
			.graduated(user.isGraduated())
			.build();
		if (user.getPrimaryMajor() != null) {
			userInformation.getCompleteDivision()
				.add(CompleteDivisionResponse.of("PRIMARY", user.getPrimaryMajor()));
		}
		if (user.getDualMajor() != null) {
			userInformation.getCompleteDivision()
				.add(CompleteDivisionResponse.of("DUAL", user.getDualMajor()));
		}
		if (user.getSubMajor() != null) {
			userInformation.getCompleteDivision()
				.add(CompleteDivisionResponse.of("SUB", user.getSubMajor()));
		}
		return userInformation;
	}
}
