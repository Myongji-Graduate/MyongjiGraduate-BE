package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.HUMANITIES;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.ICT;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.LAW;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.findBelongingCollege;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class DefaultBasicAcademicalGraduationManager implements BasicAcademicalGraduationManager {

	@Override
	public boolean isSatisfied(String major) {
		return List.of(HUMANITIES, LAW, ICT)
			.contains(findBelongingCollege(major));
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory,
		Set<BasicAcademicalCultureLecture> graduationLectures, int basicAcademicalCredit) {
		Set<Lecture> basicAcademicalLectures = convertToLectureSet(graduationLectures);

		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> basicAcademicalLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			"학문기초교양", true, basicAcademicalCredit);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);

		return DetailGraduationResult.createNonCategorizedGraduationResult(basicAcademicalCredit,
			List.of(detailCategoryResult));
	}
}
