package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureQueryRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class LecturePersistenceAdapter implements FindLecturePort, SearchLecturePort {

	private final LectureQueryRepository lectureQueryRepository;
	private final LectureRepository lectureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes) {
		List<LectureJpaEntity> lectureJpaEntities = lectureRepository.findByLectureCodeIn(lectureCodes);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureModel)
			.collect(Collectors.toList());
	}

	@Override
	public List<Lecture> findLecturesByIds(List<Long> lectureIds) {
		List<LectureJpaEntity> lectureJpaEntities = lectureRepository.findByIdIn(lectureIds);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureModel)
			.collect(Collectors.toList());
	}

	@Override
	public List<Lecture> searchLectureByNameOrCode(String type, String keyword) {
		List<LectureJpaEntity> lectureJpaEntities = lectureQueryRepository.searchByNameOrCode(type, keyword);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureModel)
			.collect(Collectors.toList());
	}
}
