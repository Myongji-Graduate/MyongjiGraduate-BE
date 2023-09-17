package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindCommonCulturePort;
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
		return commonCultureRepository.findAllByEntryYear(user.getEntryYear()).stream()
			.map(lectureMapper::mapToCommonCultureModel)
			.collect(Collectors.toSet());
	}
}
