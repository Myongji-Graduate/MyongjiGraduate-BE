package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class FindMajorPersistenceAdapter implements FindMajorPort {

	private final MajorLectureRepository majorLectureRepository;
	private final LectureMapper mapper;

	@Override
	public Set<MajorLecture> findMajor(String major) {
		return majorLectureRepository.findAllByMajor(major)
			.stream()
			.map(mapper::mapToMajorLectureModel)
			.collect(Collectors.toSet());
	}
}
