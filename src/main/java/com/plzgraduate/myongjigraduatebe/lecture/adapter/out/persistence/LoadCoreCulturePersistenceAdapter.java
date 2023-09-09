package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.CoreCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class LoadCoreCulturePersistenceAdapter implements LoadCoreCulturePort {

	private final CoreCultureRepository coreCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<CoreCulture> loadCoreCulture(User user) {
		return coreCultureRepository.findAllByEntryYear(user.getEntryYear()).stream()
			.map(lectureMapper::mapToDomainCoreCultureModel)
			.collect(Collectors.toSet());
	}

}
