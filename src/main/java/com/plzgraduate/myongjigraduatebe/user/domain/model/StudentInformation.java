package com.plzgraduate.myongjigraduatebe.user.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StudentInformation {

	private final int entryYear;
	private String department;
	private StudentCategory studentCategory;

	@Builder
	private StudentInformation(int entryYear, String department, StudentCategory studentCategory) {
		this.entryYear = entryYear;
		this.department = department;
		this.studentCategory = studentCategory;
	}

	public static StudentInformation of(int entryYear, String department, StudentCategory studentCategory) {
		return StudentInformation.builder()
			.entryYear(entryYear)
			.department(department)
			.studentCategory(studentCategory)
			.build();
	}

	public boolean checkBeforeEntryYear(int entryYear) {
		return this.entryYear < entryYear;
	}

	public boolean checkDepartment(String department) {
		return this.department.equals(department);
	}
}
