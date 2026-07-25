package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel.BASIC;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel.ENG12;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel.ENG34;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class FindCommonCulturePersistenceAdapter implements FindCommonCulturePort {
	private static final int DIGITAL_LITERACY_START_ENTRY_YEAR = 23;
	private static final String CAREER_RECOGNITION_CODE = "KMA02137";

	private final CommonCultureRepository commonCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<CommonCulture> findCommonCulture(User user) {
		if (user.getEnglishLevel() == BASIC) {
			return findEngBasicCommonCultures(user);
		}
		if (user.getEnglishLevel() == ENG12) {
			return findEng12CommonCultures(user);
		}
		if (user.getEnglishLevel() == ENG34) {
			return findEng34CommonCultures(user);
		}
		return findEngFreeCommonCultures(user);
	}

	private Set<CommonCulture> findEngBasicCommonCultures(User user) {
		return commonCultureRepository.findEngBasicGraduationCommonCulturesByEntryYear(user.getEntryYear())
			.stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.map(commonCulture -> normalizeCareerCategory(user, commonCulture))
			.collect(Collectors.toSet());
	}

	private Set<CommonCulture> findEng12CommonCultures(User user) {
		return commonCultureRepository.findEng12GraduationCommonCulturesByEntryYear(user.getEntryYear())
			.stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.map(commonCulture -> normalizeCareerCategory(user, commonCulture))
			.collect(Collectors.toSet());
	}

	private Set<CommonCulture> findEng34CommonCultures(User user) {
		return commonCultureRepository.findEng34GraduationCommonCulturesByEntryYear(user.getEntryYear())
			.stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.map(commonCulture -> normalizeCareerCategory(user, commonCulture))
			.collect(Collectors.toSet());
	}

	private Set<CommonCulture> findEngFreeCommonCultures(User user) {
		return commonCultureRepository.findEngFreeGraduationCommonCulturesByEntryYear(user.getEntryYear())
			.stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.map(commonCulture -> normalizeCareerCategory(user, commonCulture))
			.collect(Collectors.toSet());
	}

	private CommonCulture normalizeCareerCategory(User user, CommonCulture commonCulture) {
		if (user.getEntryYear() >= DIGITAL_LITERACY_START_ENTRY_YEAR
			&& commonCulture.getCommonCultureCategory() == CommonCultureCategory.CAREER
			&& CAREER_RECOGNITION_CODE.equals(commonCulture.getLecture().getRecognitionCode())) {
			return CommonCulture.of(
				commonCulture.getLecture(), CommonCultureCategory.DIGITAL_LITERACY);
		}
		return commonCulture;
	}
}
