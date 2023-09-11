package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class LecturePersistenceAdaptor implements LoadLecturePort {

	private final LectureRepository lectureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public List<Lecture> loadLecturesByLectureCodes(List<String> lectureCodes) {
		List<LectureJpaEntity> lectureJpaEntities = lectureRepository.findByLectureCodeIn(lectureCodes);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureDomainEntity)
			.collect(Collectors.toList());
	}
}