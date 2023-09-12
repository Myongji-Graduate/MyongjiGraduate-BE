package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class LoadBasicAcademicalCulturePersistenceAdapter implements LoadBasicAcademicalCulturePort {

	private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<BasicAcademicalCulture> loadBasicAcademicalCulture(User user) {
		College userCollege = College.findBelongingCollege(user.getMajor());
		return basicAcademicalCultureRepository.findAllByCollege(userCollege.getText()).stream()
			.map(lectureMapper::mapToDomainBasicAcademicalCultureModel)
			.collect(Collectors.toSet());
	}
}
