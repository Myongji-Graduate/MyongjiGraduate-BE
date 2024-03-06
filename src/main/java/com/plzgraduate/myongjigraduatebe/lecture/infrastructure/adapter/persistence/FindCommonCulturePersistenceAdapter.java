package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel.*;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class FindCommonCulturePersistenceAdapter implements FindCommonCulturePort {

	private final CommonCultureRepository commonCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<CommonCulture> findCommonCulture(User user) {
		if (user.getEnglishLevel() == ENG12) {
			return findEng12CommonCultures(user);
		}
		if (user.getEnglishLevel() == ENG34) {
			return findEng34CommonCultures(user);
		}
		return findEngFreeCommonCultures(user);
	}

	private Set<CommonCulture> findEng12CommonCultures(User user) {
		return commonCultureRepository.findEng12GraduationCommonCulturesByEntryYear(user.getEntryYear()).stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.collect(Collectors.toSet());
	}

	private Set<CommonCulture> findEng34CommonCultures(User user) {
		return commonCultureRepository.findEng34GraduationCommonCulturesByEntryYear(user.getEntryYear()).stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.collect(Collectors.toSet());
	}

	private Set<CommonCulture> findEngFreeCommonCultures(User user) {
		return commonCultureRepository.findEngFreeGraduationCommonCulturesByEntryYear(user.getEntryYear()).stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.collect(Collectors.toSet());
	}
}
