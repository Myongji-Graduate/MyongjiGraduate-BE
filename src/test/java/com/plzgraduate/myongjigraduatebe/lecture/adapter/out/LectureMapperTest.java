package com.plzgraduate.myongjigraduatebe.lecture.adapter.out;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

@Import({LectureMapper.class})
class LectureMapperTest extends PersistenceTestSupport {


	@DisplayName("JPA 엔티티를 도메인 엔티티로 변환")
	@Test
	void mapToLectureDomainEntityTest() {
	    //given

	    //when

	    //then

	}

	@DisplayName("도메인 엔티티를 JPA 엔티티로 변환")
	@Test
	void test() {
	    //given

	    //when

	    //then

	}

}
