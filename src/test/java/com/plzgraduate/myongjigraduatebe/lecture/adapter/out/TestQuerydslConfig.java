package com.plzgraduate.myongjigraduatebe.lecture.adapter.out;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

@TestConfiguration
public class TestQuerydslConfig {
	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public LectureQueryRepository lectureQueryRepository() {
		return new LectureQueryRepository(jpaQueryFactory());
	}

}
