package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 2026-1 기준 철학과 전공필수는 전 학번 해제되었다.
 * 과거의 "답사/대체과목" 특례는 더 이상 전필 목록에 주입하지 않는다.
 */

@Component
public class ReplaceMandatoryMajorHandler implements MandatoryMajorSpecialCaseHandler {

	@Override
	public Optional<MandatorySpecialCaseInformation> evaluate(User user,
		MajorType majorType, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures,
		List<OptionalMandatoryPolicy> optionalMandatoryPolicies) {
		return Optional.empty();
	}
}
