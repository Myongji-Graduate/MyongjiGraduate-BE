package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.MajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class FindMajorPersistenceAdapter implements FindMajorPort {

	private final MajorLectureRepository majorLectureRepository;
	private final LectureMapper mapper;

	@Override
	public Set<MajorLecture> findMajor(String major) {
		return majorLectureRepository.findAllByMajor(major).stream()
			.map(mapper::mapToMajorLectureModel)
			.collect(Collectors.toSet());
	}
}
