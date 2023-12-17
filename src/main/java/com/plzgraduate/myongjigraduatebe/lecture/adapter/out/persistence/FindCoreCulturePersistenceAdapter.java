package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class FindCoreCulturePersistenceAdapter implements FindCoreCulturePort {

	private final CoreCultureRepository coreCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<CoreCulture> findCoreCulture(User user) {
		return coreCultureRepository.findAllByEntryYear(user.getEntryYear()).stream()
			.map(lectureMapper::mapToCoreCultureModel)
			.collect(Collectors.toSet());
	}

}
