package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.save;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SaveTakenLectureCommand {
	private final User user;
	private final List<TakenLectureInformation> takenLectureInformationList;

	@Builder
	private SaveTakenLectureCommand(User user, List<TakenLectureInformation> takenLectureInformationList) {
		this.user = user;
		this.takenLectureInformationList = takenLectureInformationList;
	}

	public static SaveTakenLectureCommand of(User user, List<TakenLectureInformation> takenLectureInformationList) {
		return SaveTakenLectureCommand.builder()
			.user(user)
			.takenLectureInformationList(takenLectureInformationList)
			.build();
	}

	public static TakenLectureInformation createTakenLectureInformation(String lectureCode, int year, Semester semester) {
		return TakenLectureInformation.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(semester)
			.build();
	}

	@Getter
	public static class TakenLectureInformation {
		private final String lectureCode;
		private final int year;
		private final Semester semester;

		@Builder
		private TakenLectureInformation(String lectureCode, int year, Semester semester) {
			this.lectureCode = lectureCode;
			this.year = year;
			this.semester = semester;
		}
	}
}
