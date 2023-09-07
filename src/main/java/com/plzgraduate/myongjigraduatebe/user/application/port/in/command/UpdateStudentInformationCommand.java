package com.plzgraduate.myongjigraduatebe.user.application.port.in.command;

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

	private String subMajor;

	private StudentCategory studentCategory;

	@Builder
	private UpdateStudentInformationCommand(User user, String name, String major, String subMajor,
		StudentCategory studentCategory) {
		this.user = user;
		this.name = name;
		this.major = major;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
	}

	public static UpdateStudentInformationCommand of(User user, String name, String major, String subMajor,
		StudentCategory studentCategory) {
		return UpdateStudentInformationCommand.builder()
			.user(user)
			.name(name)
			.major(major)
			.studentCategory(studentCategory)
			.build();
	}
}
