package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class FindBasicAcademicalCulturePersistenceAdapter implements FindBasicAcademicalCulturePort {

	private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(User user) {
		College userCollege = College.findBelongingCollege(user);
		return basicAcademicalCultureRepository.findAllByCollege(userCollege.getName()).stream()
			.map(lectureMapper::mapToBasicAcademicalCultureLectureModel)
			.collect(Collectors.toSet());
	}
}
