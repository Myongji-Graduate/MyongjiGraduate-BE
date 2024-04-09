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

	private String changeMajor;

	private String dualMajor;

	private String subMajor;

	private StudentCategory studentCategory;

	@Builder
	private UpdateStudentInformationCommand(User user, String name, String major, String changeMajor, String dualMajor,
		String subMajor, StudentCategory studentCategory) {
		this.user = user;
		this.name = name;
		this.major = major;
		this.changeMajor = changeMajor;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
	}

	public static UpdateStudentInformationCommand of(User user, String name, String major, String changeMajor,
		String dualMajor, String subMajor, StudentCategory studentCategory) {
		return UpdateStudentInformationCommand.builder()
			.user(user)
			.name(name)
			.major(major)
			.changeMajor(changeMajor)
			.dualMajor(dualMajor)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
