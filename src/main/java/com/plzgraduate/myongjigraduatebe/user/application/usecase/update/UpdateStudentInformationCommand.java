package com.plzgraduate.myongjigraduatebe.user.application.usecase.update;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class
UpdateStudentInformationCommand {

	private User user;

	private String name;

	private String major;

	private String dualMajor;

	private String subMajor;

	private String associatedMajor;

	private Integer completedSemesterCount;

	private TransferCredit transferCredit;

	private ExchangeCredit exchangeCredit;

	private StudentCategory studentCategory;

	private int totalCredit;

	private double takenCredit;

	private boolean graduate;

	private boolean honorsCollege;

	private String honorsTargetMajor;

	@Builder
	private UpdateStudentInformationCommand(User user, String name, String major, String dualMajor,
											String subMajor, String associatedMajor, Integer completedSemesterCount, TransferCredit transferCredit, ExchangeCredit exchangeCredit, StudentCategory studentCategory, int totalCredit, double takenCredit,
										boolean graduate, boolean honorsCollege, String honorsTargetMajor) {
		this.user = user;
		this.name = name;
		this.major = major;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.completedSemesterCount = completedSemesterCount;
		this.transferCredit = transferCredit;
		this.exchangeCredit = exchangeCredit;
		this.studentCategory = studentCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduate = graduate;
		this.honorsCollege = honorsCollege;
		this.honorsTargetMajor = honorsTargetMajor;
	}

	public static UpdateStudentInformationCommand of(User user,
		ParsingInformation parsingInformation, boolean honorsCollege, String honorsTargetMajor) {
		return UpdateStudentInformationCommand.builder()
				.user(user)
				.name(parsingInformation.getStudentName())
				.major(parsingInformation.getMajor())
				.dualMajor(parsingInformation.getDualMajor())
				.subMajor(parsingInformation.getSubMajor())
				.associatedMajor(parsingInformation.getAssociatedMajor())
				.completedSemesterCount(parsingInformation.getCompletedSemesterCount())
				.transferCredit(parsingInformation.getTransferCredit())
				.exchangeCredit(parsingInformation.getExchangeCredit())
				.studentCategory(parsingInformation.getStudentCategory())
				.honorsCollege(honorsCollege)
				.honorsTargetMajor(honorsCollege ? honorsTargetMajor : null)
				.build();
	}

	public static UpdateStudentInformationCommand of(User user, ParsingInformation parsingInformation) {
		return of(user, parsingInformation, user.isHonorsCollege(), user.getHonorsTargetMajor());
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
				.associatedMajor(user.getAssociatedMajor())
				.completedSemesterCount(user.getCompletedSemesterCount())
				.totalCredit(graduationResult.getTotalCredit())
				.takenCredit(graduationResult.getTakenCredit())
				.graduate(graduationResult.isGraduated())
				.honorsCollege(user.isHonorsCollege())
				.honorsTargetMajor(user.getHonorsTargetMajor())
				.build();
	}
}
