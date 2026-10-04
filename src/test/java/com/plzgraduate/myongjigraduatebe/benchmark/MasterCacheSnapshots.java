package com.plzgraduate.myongjigraduatebe.benchmark;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import java.io.Serializable;
import java.util.List;

// Cache immutable values; calculations receive fresh, independently mutable domain objects.
final class MasterCacheSnapshots {
    private MasterCacheSnapshots() {}

    record Common(Lecture lecture, CommonCultureCategory category) implements Serializable {
        static Common from(CommonCulture value) {
            return new Common(value.getLecture(), value.getCommonCultureCategory());
        }
        CommonCulture toDomain() { return CommonCulture.of(lecture, category); }
    }

    record Core(Lecture lecture, CoreCultureCategory category) implements Serializable {
        static Core from(CoreCulture value) {
            return new Core(value.getLecture(), value.getCoreCultureCategory());
        }
        CoreCulture toDomain() { return CoreCulture.of(lecture, category); }
    }

    record Major(Lecture lecture, String major, int mandatory, int start, int end) implements Serializable {
        static Major from(MajorLecture value) {
            return new Major(value.getLecture(), value.getMajor(), value.getIsMandatory(),
                value.getAppliedStartEntryYear(), value.getAppliedEndEntryYear());
        }
        MajorLecture toDomain() { return MajorLecture.of(lecture, major, mandatory, start, end); }
    }

    record Basic(Lecture lecture, String college, String sourcePolicyKey, String major,
                 Integer startEntryYear, Integer endEntryYear, Integer startTakenYear,
                 Semester startTakenSemester, Integer endTakenYear, Semester endTakenSemester)
        implements Serializable {
        static Basic from(BasicAcademicalCultureLecture value) {
            return new Basic(value.getLecture(), value.getCollege(), value.getSourcePolicyKey(),
                value.getMajor(), value.getStartEntryYear(), value.getEndEntryYear(),
                value.getStartTakenYear(), value.getStartTakenSemester(),
                value.getEndTakenYear(), value.getEndTakenSemester());
        }
        BasicAcademicalCultureLecture toDomain() {
            return BasicAcademicalCultureLecture.builder().lecture(lecture).college(college)
                .sourcePolicyKey(sourcePolicyKey).major(major).startEntryYear(startEntryYear)
                .endEntryYear(endEntryYear).startTakenYear(startTakenYear)
                .startTakenSemester(startTakenSemester).endTakenYear(endTakenYear)
                .endTakenSemester(endTakenSemester).build();
        }
    }

    record Candidate(Lecture lecture, String equivalenceKey) implements Serializable {}

    record Policy(Long id, String name, String major, int requiredCount, int requiredCredit,
                  List<Candidate> candidates) implements Serializable {
        Policy { candidates = List.copyOf(candidates); }
        static Policy from(OptionalMandatoryPolicy value) {
            return new Policy(value.getId(), value.getName(), value.getMajor(),
                value.getRequiredCount(), value.getRequiredCredit(), value.getCandidateLectures().stream()
                    .map(candidate -> new Candidate(candidate.lecture(), candidate.equivalenceKey())).toList());
        }
        OptionalMandatoryPolicy toDomain() {
            return OptionalMandatoryPolicy.builder().id(id).name(name).major(major)
                .requiredCount(requiredCount).requiredCredit(requiredCredit)
                .candidateLectures(candidates.stream().map(candidate ->
                    new OptionalMandatoryPolicy.CandidateLecture(candidate.lecture(), candidate.equivalenceKey()))
                    .toList()).build();
        }
    }
}
