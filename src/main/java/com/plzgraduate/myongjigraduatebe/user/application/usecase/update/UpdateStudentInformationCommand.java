package com.plzgraduate.myongjigraduatebe.user.application.usecase.update;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateStudentInformationCommand {

	private User user;

	private String name;

	private String major;

	private String dualMajor;

	private String subMajor;

	private StudentCategory studentCategory;

	private int totalCredit;

	private double takenCredit;

	private boolean graduate;

	@Builder
	private UpdateStudentInformationCommand(User user, String name, String major, String dualMajor,
		String subMajor, StudentCategory studentCategory, int totalCredit, double takenCredit,
		boolean graduate) {
		this.user = user;
		this.name = name;
		this.major = major;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduate = graduate;
	}

	public static UpdateStudentInformationCommand of(User user,
		ParsingInformation parsingInformation) {
		return UpdateStudentInformationCommand.builder()
			.user(user)
			.name(parsingInformation.getStudentName())
			.major(parsingInformation.getMajor())
			.dualMajor(parsingInformation.getDualMajor())
			.subMajor(parsingInformation.getSubMajor())
			.studentCategory(parsingInformation.getStudentCategory())
			.build();
	}

	public static UpdateStudentInformationCommand update(User user,
		GraduationResult graduationResult) {
		return UpdateStudentInformationCommand.builder()
			.user(user)
			.name(user.getName())
			.studentCategory(user.getStudentCategory())
			.major(user.getPrimaryMajor())
			.dualMajor(user.getDualMajor())
			.subMajor(user.getSubMajor())
			.totalCredit(graduationResult.getTotalCredit())
			.takenCredit(graduationResult.getTakenCredit())
			.graduate(graduationResult.isGraduated())
			.build();
	}
}
