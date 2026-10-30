package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/** Credentials never enter Redis. Course fields are immutable within the DB catalog revision. */
public record TakenLectureSnapshot(Long userId, long revision, long catalogRevision, List<Entry> entries) {
    public TakenLectureSnapshot {
        if (userId == null || userId <= 0 || revision < 0 || catalogRevision < 0 || entries == null) {
            throw new IllegalArgumentException("Invalid taken lecture snapshot");
        }
        entries = List.copyOf(entries);
    }

    public static TakenLectureSnapshot from(Long userId, long revision, long catalogRevision, List<TakenLecture> values) {
        return new TakenLectureSnapshot(userId, revision, catalogRevision, values.stream().map(value -> new Entry(
            value.getId(), value.getLecture().getId(), value.getYear(), value.getSemester(), value.getCreatedAt(),
            Course.from(value.getLecture()))).sorted(Comparator.comparing(Entry::id)).toList());
    }

    public List<TakenLecture> toDomain(User user) {
        if (!userId.equals(user.getId())) throw new IllegalArgumentException("Snapshot owner mismatch");
        return entries.stream().map(entry -> TakenLecture.builder().id(entry.id()).user(user)
            .lecture(entry.course().toDomain(entry.lectureId())).year(entry.year())
            .semester(entry.semester()).createdAt(entry.createdAt()).build()).toList();
    }

    public record Entry(Long id, String lectureId, Integer year, Semester semester, Instant createdAt, Course course) {
        public Entry {
            if (id == null || id <= 0 || lectureId == null || lectureId.isBlank() || course == null) {
                throw new IllegalArgumentException("Invalid taken lecture entry");
            }
        }
    }

    public record Course(String name, int credit, String duplicateCode, int isRevoked) {
        static Course from(Lecture lecture) {
            return new Course(lecture.getName(), lecture.getCredit(), lecture.getDuplicateCode(), lecture.getIsRevoked());
        }
        Lecture toDomain(String id) {
            return Lecture.builder().id(id).name(name).credit(credit).duplicateCode(duplicateCode).isRevoked(isRevoked).build();
        }
    }
}
