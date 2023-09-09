package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.BasicAcademicalCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.MajorJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Major;

@Component
public class LectureMapper {

	Lecture mapToLectureDomainEntity(LectureJpaEntity lecture) {

		return Lecture.builder()
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}

	LectureJpaEntity mapToLectureJpaEntity(Lecture lecture) {

		return LectureJpaEntity.builder()
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked())
			.duplicateCode(lecture.getDuplicateCode())
			.build();
	}

	CommonCulture mapToDomainCommonCultureModel(CommonCultureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return CommonCulture.builder()
			.lecture(
				Lecture.builder()
					.lectureCode(lectureJpaEntity.getLectureCode())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.commonCultureCategory(entity.getCommonCultureCategory())
			.build();
	}

	CoreCulture mapToDomainCoreCultureModel(CoreCultureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return CoreCulture.builder()
			.lecture(
				Lecture.builder()
					.lectureCode(lectureJpaEntity.getLectureCode())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.coreCultureCategory(entity.getCoreCultureCategory())
			.build();
	}

	BasicAcademicalCulture mapToDomainBasicAcademicalCultureModel(BasicAcademicalCultureJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return BasicAcademicalCulture.builder()
			.lecture(
				Lecture.builder()
					.lectureCode(lectureJpaEntity.getLectureCode())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.college(entity.getCollege())
			.build();
	}

	Major mapToDomainMajorModel(MajorJpaEntity entity) {
		LectureJpaEntity lectureJpaEntity = entity.getLectureJpaEntity();
		return Major.builder()
			.lecture(
				Lecture.builder()
					.lectureCode(lectureJpaEntity.getLectureCode())
					.name(lectureJpaEntity.getName())
					.credit(lectureJpaEntity.getCredit())
					.duplicateCode(lectureJpaEntity.getDuplicateCode())
					.isRevoked(lectureJpaEntity.getIsRevoked())
					.build()
			)
			.major(entity.getMajor())
			.appliedStartEntryYear(entity.getStartEntryYear())
			.appliedEndEntryYear(entity.getEndEntryYear())
			.isMandatory(entity.getMandatory()).build();
	}
}
