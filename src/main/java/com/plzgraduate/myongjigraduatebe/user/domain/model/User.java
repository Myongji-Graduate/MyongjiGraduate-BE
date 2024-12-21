package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType.DUAL;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType.PRIMARY;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class User {

	private final Long id;
	private final String authId;
	private final EnglishLevel englishLevel;
	private final String studentNumber;
	private final int entryYear;
	private final Instant createdAt;
	private final Instant updatedAt;
	private TransferCredit transferCredit;
	private String password;
	private String name;
	private String primaryMajor;
	private String subMajor;
	private String dualMajor;
	private String associatedMajor;
	private StudentCategory studentCategory;
	private int totalCredit;
	private double takenCredit;
	private boolean graduated;

	@Builder
	private User(
            Long id,
            String authId,
            String password,
            EnglishLevel englishLevel,
            String name,
            String studentNumber,
            int entryYear,
            String primaryMajor,
            String subMajor,
            String dualMajor,
            String associatedMajor,
			TransferCredit transferCredit,
			StudentCategory studentCategory,
            int totalCredit,
            double takenCredit,
            boolean graduated,
            Instant createdAt,
            Instant updatedAt
    ) {
		this.id = id;
		this.authId = authId;
		this.password = password;
		this.englishLevel = englishLevel;
		this.name = name;
		this.studentNumber = studentNumber;
		this.entryYear = entryYear;
		this.primaryMajor = primaryMajor;
		this.subMajor = subMajor;
		this.dualMajor = dualMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
		this.transferCredit = transferCredit != null ? transferCredit : TransferCredit.empty();
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduated;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
    }

	public static User create(
		String authId,
		String password,
		EnglishLevel englishLevel,
		String studentNumber
	) {
		return User.builder()
			.authId(authId).password(password).englishLevel(englishLevel)
			.studentNumber(studentNumber).entryYear(parseEntryYearInStudentNumber(studentNumber))
			.totalCredit(0).takenCredit(0).graduated(false).build();
	}

	public static User createAnonymous(
		EnglishLevel englishLevel,
		String name,
		String studentNumber,
		String primaryMajor,
		String subMajor,
		String dualMajor,
		String associatedMajor,
		StudentCategory studentCategory,
        TransferCredit transferCredit) {
		return User.builder()
			.authId("anonymous")
			.name(name)
			.englishLevel(englishLevel)
			.studentNumber(studentNumber)
			.entryYear(parseEntryYearInStudentNumber(studentNumber))
			.primaryMajor(primaryMajor)
			.subMajor(subMajor)
			.dualMajor(dualMajor)
			.associatedMajor(associatedMajor)
			.studentCategory(studentCategory)
			.totalCredit(0)
			.takenCredit(0)
			.graduated(false)
			.transferCredit(transferCredit)
			.build();
	}

	private static int parseEntryYearInStudentNumber(String studentNumber) {
		return Integer.parseInt(studentNumber.substring(2, 4));
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", authId='" + authId + '\'' +
			", englishLevel=" + englishLevel +
			", studentNumber='" + studentNumber + '\'' +
			", entryYear=" + entryYear +
			", createdAt=" + createdAt +
			", updatedAt=" + updatedAt +
			", password='" + password + '\'' +
			", name='" + name + '\'' +
			", primaryMajor='" + primaryMajor + '\'' +
			", subMajor='" + subMajor + '\'' +
			", dualMajor='" + dualMajor + '\'' +
			", associatedMajor='" + associatedMajor + '\'' +
			", transferCredit=" + transferCredit +
			", studentCategory=" + studentCategory +
			", totalCredit=" + totalCredit +
			", takenCredit=" + takenCredit +
			", graduated=" + graduated +
			'}';
	}

	public void updateStudentInformation(
		String name, String major, String dualMajor,
		String subMajor, String associatedMajor,
		StudentCategory studentCategory, int totalCredit, double takenCredit, boolean graduate
	) {
		this.name = name;
		this.primaryMajor = major;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduate;
	}

	public void updateTransferCredit(TransferCredit transferCredit) {
		if (transferCredit != null) {
			this.transferCredit = transferCredit;
		}
	}
	public boolean checkBeforeEntryYear(int entryYear) {
		return this.entryYear < entryYear;
	}

	public boolean checkMajor(String major) {
		return this.primaryMajor.equals(major);
	}

	public boolean compareStudentNumber(String studentNumber) {
		return this.studentNumber.equals(studentNumber);
	}

	public boolean matchPassword(PasswordEncoder passwordEncoder, String password) {
		return passwordEncoder.matches(password, this.password);
	}

	public String getEncryptedAuthId() {
		return authId.replace(authId.substring(authId.length() - 3), "***");
	}

	public void resetPassword(String newPassword) {
		this.password = newPassword;
	}

	public boolean isMyAuthId(String authId) {
		return this.authId.equals(authId);
	}

	public String getMajorByMajorType(MajorType majorType) {
		if (majorType == PRIMARY) {
			return primaryMajor;
		} else if (majorType == DUAL) {
			return dualMajor;
		}
		return subMajor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		User user = (User) o;
		return Objects.equals(authId, user.authId) && Objects.equals(
			studentNumber,
			user.studentNumber
		);
	}

	@Override
	public int hashCode() {
		return Objects.hash(authId, studentNumber);
	}

}
