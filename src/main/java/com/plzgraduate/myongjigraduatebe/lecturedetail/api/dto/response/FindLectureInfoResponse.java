package com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Getter
public class FindLectureInfoResponse {

    private final String subject;
    private final String professor;
    private final String assignment;
    private final List<String> attendance;
    private final List<String> exam;
    private final String grading;
    private final String teamwork;
    private final BigDecimal rating;
    private final List<LectureReview> lectureReviews;

    @Builder
    private FindLectureInfoResponse(
            String subject,
            String professor,
            String assignment,
            List<String> attendance,
            List<String> exam,
            String grading,
            String teamwork,
            BigDecimal rating,
            List<LectureReview> lectureReviews
    ) {
        this.subject = subject;
        this.professor = professor;
        this.assignment = assignment;
        this.attendance = attendance;
        this.exam = exam;
        this.grading = grading;
        this.teamwork = teamwork;
        this.rating = rating;
        this.lectureReviews = lectureReviews;
    }

    public static FindLectureInfoResponse from(LectureInfo lectureInfo) {
        return FindLectureInfoResponse.builder()
                .subject(lectureInfo.getSubject())
                .professor(lectureInfo.getProfessor())
                .assignment(lectureInfo.getAssignment())
                .attendance(splitByComma(lectureInfo.getAttendance()))
                .exam(splitByComma(lectureInfo.getExam()))
                .grading(lectureInfo.getGrading())
                .teamwork(lectureInfo.getTeamwork())
                .rating(lectureInfo.getRating())
                .lectureReviews(lectureInfo.getLectureReviews())
                .build();
    }

    private static List<String> splitByComma(String value) {
        if (value == null) return Collections.emptyList();
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return Collections.emptyList();

        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            String inner = trimmed.substring(1, trimmed.length() - 1).trim();
            if (inner.isEmpty()) return Collections.emptyList();

            return Arrays.stream(inner.split(","))
                    .map(String::trim)
                    .map(s -> {
                        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
                            return s.substring(1, s.length() - 1);
                        }
                        return s;
                    })
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
