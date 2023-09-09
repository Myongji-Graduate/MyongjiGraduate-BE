package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserRepository;

@Import({TakenLecturePersistenceAdaptor.class, TakenLectureMapper.class})
class TakenLecturePersistenceAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private TakenLecturePersistenceAdaptor takenLecturePersistenceAdaptor;

	@Autowired
	private TakenLectureMapper takenLectureMapper;

	@Autowired
	private TakenLectureRepository takenLectureRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LectureRepository lectureRepository;

}
