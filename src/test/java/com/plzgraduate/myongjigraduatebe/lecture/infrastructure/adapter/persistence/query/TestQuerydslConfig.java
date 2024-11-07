package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.query;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

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
