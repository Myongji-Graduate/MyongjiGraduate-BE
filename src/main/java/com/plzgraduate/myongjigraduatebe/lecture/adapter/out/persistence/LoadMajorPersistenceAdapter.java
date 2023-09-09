package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.MajorRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Major;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class LoadMajorPersistenceAdapter implements LoadMajorPort {

	private final MajorRepository majorRepository;
	private final LectureMapper mapper;

	@Override
	public Set<Major> loadMajor(User user) {
		return majorRepository.findAllByMajor(user.getMajor()).stream()
			.map(mapper::mapToDomainMajorModel)
			.collect(Collectors.toSet());
	}
}
