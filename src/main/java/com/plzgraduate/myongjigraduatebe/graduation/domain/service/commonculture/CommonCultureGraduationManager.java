package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public class CommonCultureGraduationManager implements GraduationManager<CommonCulture> {

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, Set<CommonCulture> graduationLectures,
		int commonCultureGraduationTotalCredit) {
		CommonCultureDetailCategoryManager commonCultureDetailCategoryManager = new CommonCultureDetailCategoryManager();
		List<DetailCategoryResult> commonCultureDetailCategoryResults = Arrays.stream(CommonCultureCategory.values())
			.filter(
				commonCultureCategory -> commonCultureCategory.isContainsEntryYear(user.getEntryYear()))
			.map(commonCultureCategory -> commonCultureDetailCategoryManager.generate(user, takenLectureInventory,
				graduationLectures, commonCultureCategory))
			.collect(Collectors.toList());

		return DetailGraduationResult.create(COMMON_CULTURE, commonCultureGraduationTotalCredit,
			commonCultureDetailCategoryResults);
	}

}
