package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

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
