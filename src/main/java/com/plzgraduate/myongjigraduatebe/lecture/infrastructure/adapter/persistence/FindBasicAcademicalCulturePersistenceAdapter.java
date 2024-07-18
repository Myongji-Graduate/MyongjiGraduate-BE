package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class FindBasicAcademicalCulturePersistenceAdapter implements FindBasicAcademicalCulturePort {

	private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(String major) {
		College userCollege = College.findBelongingCollege(major);
		return basicAcademicalCultureRepository.findAllByCollege(userCollege.getName()).stream()
			.map(lectureMapper::mapToBasicAcademicalCultureLectureModel)
			.collect(Collectors.toSet());
	}

	@Override
	public Set<BasicAcademicalCultureLecture> findDuplicatedLecturesBetweenMajors(User user) {
		College primaryMajorCollage = College.findBelongingCollege(user.getPrimaryMajor());
		College dualMajorCollage = College.findBelongingCollege(user.getDualMajor());
		return basicAcademicalCultureRepository.findAllDuplicatedTakenByCollages(user.getId(),
				primaryMajorCollage.getName(), dualMajorCollage.getName()).stream()
			.map(lectureMapper::mapToBasicAcademicalCultureLectureModel)
			.collect(Collectors.toSet());
	}

}
