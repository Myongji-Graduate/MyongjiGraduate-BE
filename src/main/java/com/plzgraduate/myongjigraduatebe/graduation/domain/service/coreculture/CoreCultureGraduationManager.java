package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CORE_CULTURE;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public class CoreCultureGraduationManager implements GraduationManager<CoreCulture> {
	@Override
	public DetailGraduationResult createDetailGraduationResult(StudentInformation studentInformation,
		TakenLectureInventory takenLectureInventory, Set<CoreCulture> graduationLectures, int coreCultureGraduationTotalCredit) {
		CoreCultureDetailCategoryManager coreCultureDetailCategoryManager = new CoreCultureDetailCategoryManager();
		List<DetailCategoryResult> coreCultureDetailCategoryResults = Arrays.stream(CoreCultureCategory.values())
			.map(coreCultureCategory -> coreCultureDetailCategoryManager.generate(studentInformation, takenLectureInventory,
				graduationLectures, coreCultureCategory))
			.collect(Collectors.toList());

		return DetailGraduationResult.create(CORE_CULTURE, coreCultureGraduationTotalCredit,
			coreCultureDetailCategoryResults);
	}
}
