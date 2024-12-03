package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LectureMapper {

	public Lecture mapToLectureModel(LectureJpaEntity lecture) {

		return Lecture.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}

	public CommonCulture mapToCommonCultureModel(CommonCultureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return CommonCulture.builder()
			.lecture(
				Lecture.builder()
					.id(lectureJpaEntity.getId())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.commonCultureCategory(entity.getCommonCultureCategory())
			.build();
	}

	public CoreCulture mapToCoreCultureModel(CoreCultureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return CoreCulture.builder()
			.lecture(
				Lecture.builder()
					.id(lectureJpaEntity.getId())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.coreCultureCategory(entity.getCoreCultureCategory())
			.build();
	}

	public BasicAcademicalCultureLecture mapToBasicAcademicalCultureLectureModel(
		BasicAcademicalCultureLectureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return BasicAcademicalCultureLecture.builder()
			.lecture(
				Lecture.builder()
					.id(lectureJpaEntity.getId())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.college(entity.getCollege())
			.build();
	}

	public MajorLecture mapToMajorLectureModel(MajorLectureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return MajorLecture.builder()
			.lecture(
				Lecture.builder()
					.id(lectureJpaEntity.getId())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.major(entity.getMajor())
			.appliedStartEntryYear(entity.getStartEntryYear())
			.appliedEndEntryYear(entity.getEndEntryYear())
			.isMandatory(entity.getMandatory())
			.build();
	}
}
