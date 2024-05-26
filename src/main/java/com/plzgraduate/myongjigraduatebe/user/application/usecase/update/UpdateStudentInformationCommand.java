package com.plzgraduate.myongjigraduatebe.user.application.usecase.update;

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
		String subMajor, StudentCategory studentCategory, int totalCredit, double takenCredit, boolean graduate) {
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

	public static UpdateStudentInformationCommand of(User user, String name, String major,
		String dualMajor, String subMajor, StudentCategory studentCategory) {
		return UpdateStudentInformationCommand.builder()
			.user(user)
			.name(name)
			.major(major)
			.dualMajor(dualMajor)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
