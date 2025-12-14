package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.AbstractMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopularLecturesService implements PopularLecturesUseCase {

    private final PopularLecturePort popularLecturePort;
    
    private void validateMajorAndEntryYear(String major, int entryYear) {
        try {
            // 전공+학번 조합이 유효(단과대 매핑 가능)한지 선제 검증
            College.findBelongingCollege(major, entryYear);
        } catch (IllegalArgumentException e) {
            // 일관된 에러 코드로 매핑하여 400(BAD_REQUEST) 응답되도록 던짐
            throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString());
        }
    }


    @Override
    public PopularLecturesInitResponse getInitPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    ) {
        // 모든 카테고리에 대해 일관된 선제 검증 수행
        validateMajorAndEntryYear(major, entryYear);
        // 카테고리별 전체 개수
        List<PopularLecturesInitResponse.SectionMeta> sections =
                popularLecturePort.getSections(major, entryYear);

        // 데이터가 전혀 없는 경우: 빈 리스트로 안전 반환
        if (sections.isEmpty()) {
            return PopularLecturesInitResponse.of(
                    sections,
                    PopularLectureCategory.ALL,
                    List.of(),
                    limit
            );
        }

        // 첫 번째 카테고리
        PopularLectureCategory firstCategory = sections.getFirst().getCategoryName();

        // 해당 카테고리 강의 목록
        List<PopularLectureDto> lectureDtos =
                popularLecturePort.getLecturesByCategory(major, entryYear, firstCategory, limit, cursor);

        List<PopularLectureResponse> lectures = lectureDtos.stream()
                .map(PopularLectureResponse::from)
                .toList();

        return PopularLecturesInitResponse.of(
                sections,
                firstCategory,
                lectures,
                limit
        );
    }

    @Override
    public PopularLecturesByCategoryResponse getPopularLecturesByCategory(
            String major,
            int entryYear,
            PopularLectureCategory category,
            int limit,
            String cursor
    ) {
        // 모든 카테고리에 대해 일관된 선제 검증 수행
        validateMajorAndEntryYear(major, entryYear);
        List<PopularLectureDto> lectureDtos =
                popularLecturePort.getLecturesByCategory(major, entryYear, category, limit, cursor);

        if (lectureDtos.isEmpty()) {
            throw new NoSuchElementException(ErrorCode.NON_EXISTED_LECTURE.toString());
        }

        List<PopularLectureResponse> lectures = lectureDtos.stream()
                .map(PopularLectureResponse::from)
                .toList();

        return PopularLecturesByCategoryResponse.of(
                category,
                lectures,
                limit
        );
    }

    @Override
    public PopularLecturesByCategoryResponse getDefaultPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    ) {
        // ALL 경로 진입 전에도 동일한 선제 검증 수행
        validateMajorAndEntryYear(major, entryYear);
        // Avoid double work: probe fixed category order and return first non-empty page
        PopularLectureCategory[] order = new PopularLectureCategory[] {
                PopularLectureCategory.BASIC_ACADEMICAL_CULTURE,
                PopularLectureCategory.CORE_CULTURE,
                PopularLectureCategory.COMMON_CULTURE,
                PopularLectureCategory.MANDATORY_MAJOR,
                PopularLectureCategory.ELECTIVE_MAJOR
        };

        return Arrays.stream(order)
                .map(cat -> new AbstractMap.SimpleEntry<>(
                        cat,
                        popularLecturePort.getLecturesByCategory(major, entryYear, cat, limit, cursor)
                ))
                .filter(e -> !e.getValue().isEmpty())
                .findFirst()
                .map(e -> {
                    List<PopularLectureResponse> lectures = e.getValue().stream()
                            .map(PopularLectureResponse::from)
                            .toList();
                    return PopularLecturesByCategoryResponse.of(e.getKey(), lectures, limit);
                })
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NON_EXISTED_LECTURE.toString()));
    }

    @Override
    public PopularLecturesByCategoryResponse getPopularLectures(
            String major,
            int entryYear,
            PopularLectureCategory category,
            int limit,
            String cursor
    ) {
        // 통합 진입점에서도 동일 검증으로 일관성 보장
        validateMajorAndEntryYear(major, entryYear);
        if (category == PopularLectureCategory.ALL) {
            return getDefaultPopularLectures(major, entryYear, limit, cursor);
        }
        return getPopularLecturesByCategory(major, entryYear, category, limit, cursor);
    }
}
