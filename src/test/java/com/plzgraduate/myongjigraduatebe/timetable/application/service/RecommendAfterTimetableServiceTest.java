package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorLectureOfferingPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.BasicCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CoreCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CommonCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import java.util.HashMap;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.RequirementSnapshotQueryPort;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecommendAfterTimetableServiceTest {

    @Mock
    private FindUserPort findUserPort;
    @Mock
    private RemainingSemesterCalculator remainingSemesterCalculator;
    @Mock
    private RecommendAfterTimetableService.CreditTargetPolicy creditTargetPolicy;
    @Mock
    private RecommendAfterTimetableService.RemainingCreditsProvider remainingCreditsProvider;
    @Mock
    private RequirementSnapshotQueryPort requirementSnapshotQueryPort;
    @Mock
    private TakenLectureQuery takenLectureQuery;
    @Mock
    private FindLecturePort findLecturePort;
    @Mock
    private RecommendedLectureExtractor recommendedLectureExtractor;
    @Mock
    private MajorLectureOfferingPort majorLectureOfferingPort;
    @Mock
    private PopularLecturePort popularLecturePort;
    @Mock
    private MajorMembershipPort majorMembershipPort;
    @Mock
    private BasicCultureMembershipPort basicCultureMembershipPort;
    @Mock
    private CoreCultureMembershipPort coreCultureMembershipPort;
    @Mock
    private CommonCultureMembershipPort commonCultureMembershipPort;

    @InjectMocks
    private RecommendAfterTimetableService sut;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .studentCategory(StudentCategory.NORMAL)
                .build();

        RemainingSemesterCalculator.NextSemester nextSemester = new RemainingSemesterCalculator.NextSemester(3, 1);

        when(findUserPort.findUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(remainingSemesterCalculator.from(any(User.class))).thenReturn(3);
        when(remainingSemesterCalculator.nextSemester(any(User.class))).thenReturn(nextSemester);
        when(takenLectureQuery.findAlreadyTakenLectureCodes(any(User.class))).thenReturn(Collections.emptySet());
        when(findLecturePort.findAllLectures()).thenReturn(Collections.emptyList());
    }

    private Object createDetailKey(GraduationCategory category, String detailName) throws Exception {
        Class<?> detailKeyClass = Class.forName(
                "com.plzgraduate.myongjigraduatebe.timetable.application.service.RecommendAfterTimetableService$DetailKey");
        Method ofMethod = detailKeyClass.getDeclaredMethod("of", GraduationCategory.class, String.class);
        return ofMethod.invoke(null, category, detailName);
    }

    @Test
    @DisplayName("makeSemesterQuota: cap이 0이거나 deficits가 비어있으면 빈 맵 반환")
    void makeSemesterQuota_emptyInput() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "makeSemesterQuota", int.class, Map.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<Object, Integer> result1 = (Map<Object, Integer>) method.invoke(sut, 0, new HashMap<>());
        assertThat(result1).isEmpty();

        @SuppressWarnings("unchecked")
        Map<Object, Integer> result2 = (Map<Object, Integer>) method.invoke(sut, 10, new HashMap<>());
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("makeSemesterQuota: 작은 deficit(3학점 이하)는 남은 학점만큼만 할당")
    void makeSemesterQuota_smallDeficits() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "makeSemesterQuota", int.class, Map.class);
        method.setAccessible(true);

        Object key1 = createDetailKey(GraduationCategory.CORE_CULTURE, "사회와공동체");
        Object key2 = createDetailKey(GraduationCategory.COMMON_CULTURE, "공통교양");
        Map<Object, Integer> deficits = new HashMap<>();
        deficits.put(key1, 2); // 작은 deficit
        deficits.put(key2, 3); // 작은 deficit

        @SuppressWarnings("unchecked")
        Map<Object, Integer> result = (Map<Object, Integer>) method.invoke(sut, 10, deficits);

        assertThat(result).containsEntry(key1, 2).containsEntry(key2, 3);
    }

    @Test
    @DisplayName("makeSemesterQuota: 큰 deficit은 비율 배분으로 할당")
    void makeSemesterQuota_largeDeficits() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "makeSemesterQuota", int.class, Map.class);
        method.setAccessible(true);

        Object key1 = createDetailKey(GraduationCategory.PRIMARY_ELECTIVE_MAJOR, "주전공선택");
        Object key2 = createDetailKey(GraduationCategory.PRIMARY_ELECTIVE_MAJOR, "주전공선택2");
        Map<Object, Integer> deficits = new HashMap<>();
        deficits.put(key1, 20); // 큰 deficit
        deficits.put(key2, 30); // 큰 deficit

        @SuppressWarnings("unchecked")
        Map<Object, Integer> result = (Map<Object, Integer>) method.invoke(sut, 15, deficits);

        // 비율 배분: key1은 20/50 * 15 = 6, key2는 30/50 * 15 = 9 (반올림)
        assertThat(result.get(key1)).isGreaterThan(0);
        assertThat(result.get(key2)).isGreaterThan(0);
        int total = result.values().stream().mapToInt(Integer::intValue).sum();
        assertThat(total).isLessThanOrEqualTo(15);
    }

    @Test
    @DisplayName("makeSemesterQuota: 작은 deficit과 큰 deficit 혼합")
    void makeSemesterQuota_mixedDeficits() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "makeSemesterQuota", int.class, Map.class);
        method.setAccessible(true);

        Object smallKey = createDetailKey(GraduationCategory.CORE_CULTURE, "사회와공동체");
        Object largeKey = createDetailKey(GraduationCategory.PRIMARY_ELECTIVE_MAJOR, "주전공선택");
        Map<Object, Integer> deficits = new HashMap<>();
        deficits.put(smallKey, 2); // 작은 deficit
        deficits.put(largeKey, 20); // 큰 deficit

        @SuppressWarnings("unchecked")
        Map<Object, Integer> result = (Map<Object, Integer>) method.invoke(sut, 15, deficits);

        // 작은 deficit은 정확히 2 할당
        assertThat(result).containsEntry(smallKey, 2);
        // 큰 deficit은 남은 13을 비율 배분
        assertThat(result.get(largeKey)).isGreaterThan(0).isLessThanOrEqualTo(13);
    }

    @Test
    @DisplayName("getApplicableCategories: StudentCategory에 따라 적절한 카테고리 반환")
    void getApplicableCategories() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "getApplicableCategories", User.class);
        method.setAccessible(true);

        User normalUser = User.builder()
                .studentCategory(StudentCategory.NORMAL)
                .build();

        @SuppressWarnings("unchecked")
        Set<GraduationCategory> result = (Set<GraduationCategory>) method.invoke(sut, normalUser);

        assertThat(result)
                .contains(GraduationCategory.COMMON_CULTURE)
                .contains(GraduationCategory.CORE_CULTURE)
                .contains(GraduationCategory.PRIMARY_MANDATORY_MAJOR);
    }

    @Test
    @DisplayName("getApplicableCategories: StudentCategory가 null이면 빈 Set 반환")
    void getApplicableCategories_nullCategory() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "getApplicableCategories", User.class);
        method.setAccessible(true);

        User userWithoutCategory = User.builder()
                .studentCategory(null)
                .build();

        @SuppressWarnings("unchecked")
        Set<GraduationCategory> result = (Set<GraduationCategory>) method.invoke(sut, userWithoutCategory);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buildPopularityRanking: popularLecturePort가 null이면 빈 맵 반환")
    void buildPopularityRanking_nullPort() throws Exception {
        RecommendAfterTimetableService serviceWithNullPort = new RecommendAfterTimetableService(
                findUserPort, remainingSemesterCalculator, creditTargetPolicy, remainingCreditsProvider,
                requirementSnapshotQueryPort, takenLectureQuery, findLecturePort,
                recommendedLectureExtractor, majorLectureOfferingPort, null,
                majorMembershipPort, basicCultureMembershipPort, coreCultureMembershipPort, commonCultureMembershipPort
        );

        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "buildPopularityRanking", User.class, Map.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) method.invoke(
                serviceWithNullPort, testUser, Collections.emptyMap());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buildPopularityRanking: detailKeyByLectureId가 비어있으면 빈 맵 반환")
    void buildPopularityRanking_emptyDetailKeys() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "buildPopularityRanking", User.class, Map.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) method.invoke(
                sut, testUser, Collections.emptyMap());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("buildPopularityRanking: 인기 강의가 있으면 랭킹 생성")
    void buildPopularityRanking_withPopularLectures() throws Exception {
        PopularLectureDto dto1 = PopularLectureDto.builder()
                .lectureId("LEC001")
                .lectureName("강의1")
                .credit(3)
                .totalCount(100L)
                .categoryName(PopularLectureCategory.CORE_CULTURE)
                .averageRating(4.5)
                .build();
        PopularLectureDto dto2 = PopularLectureDto.builder()
                .lectureId("LEC002")
                .lectureName("강의2")
                .credit(3)
                .totalCount(80L)
                .categoryName(PopularLectureCategory.CORE_CULTURE)
                .averageRating(4.3)
                .build();
        when(popularLecturePort.getPopularLecturesByTotalCount())
                .thenReturn(List.of(dto1, dto2));

        Object key1 = createDetailKey(GraduationCategory.CORE_CULTURE, "핵심교양");
        Map<String, Object> detailKeyByLectureId = new HashMap<>();
        detailKeyByLectureId.put("LEC001", key1);
        detailKeyByLectureId.put("LEC002", key1);

        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "buildPopularityRanking", User.class, Map.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Integer> result = (Map<String, Integer>) method.invoke(
                sut, testUser, detailKeyByLectureId);

        assertThat(result)
                .isNotEmpty()
                .containsKey("LEC001")
                .containsKey("LEC002");
        assertThat(result.get("LEC001")).isLessThan(result.get("LEC002")); // 더 인기있는 강의가 낮은 랭크
    }

    @Test
    @DisplayName("shouldProcessPopularLecture: null dto는 false 반환")
    void shouldProcessPopularLecture_nullDto() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "shouldProcessPopularLecture", PopularLectureDto.class, Map.class, Set.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(sut, null, new HashMap<>(), new HashSet<>());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldProcessPopularLecture: 이미 ranking에 있으면 false 반환")
    void shouldProcessPopularLecture_alreadyInRanking() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "shouldProcessPopularLecture", PopularLectureDto.class, Map.class, Set.class);
        method.setAccessible(true);

        PopularLectureDto dto = PopularLectureDto.builder()
                .lectureId("LEC001")
                .categoryName(PopularLectureCategory.CORE_CULTURE)
                .build();
        Map<String, Integer> ranking = new HashMap<>();
        ranking.put("LEC001", 1);

        boolean result = (boolean) method.invoke(sut, dto, ranking, new HashSet<>());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldProcessPopularLecture: BASIC_ACADEMICAL_CULTURE는 false 반환")
    void shouldProcessPopularLecture_basicAcademicalCulture() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "shouldProcessPopularLecture", PopularLectureDto.class, Map.class, Set.class);
        method.setAccessible(true);

        PopularLectureDto dto = PopularLectureDto.builder()
                .lectureId("LEC001")
                .categoryName(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE)
                .build();

        boolean result = (boolean) method.invoke(sut, dto, new HashMap<>(), new HashSet<>());

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("shouldProcessPopularLecture: CORE_CULTURE는 true 반환")
    void shouldProcessPopularLecture_coreCulture() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "shouldProcessPopularLecture", PopularLectureDto.class, Map.class, Set.class);
        method.setAccessible(true);

        PopularLectureDto dto = PopularLectureDto.builder()
                .lectureId("LEC001")
                .categoryName(PopularLectureCategory.CORE_CULTURE)
                .build();

        boolean result = (boolean) method.invoke(sut, dto, new HashMap<>(), new HashSet<>());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("addGlobalPopularLectures: MAX_POPULARITY_CONSIDERED 도달 시 중단")
    void addGlobalPopularLectures_stopsAtMax() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "addGlobalPopularLectures", List.class, Set.class, Map.class, int.class);
        method.setAccessible(true);

        PopularLectureDto dto1 = PopularLectureDto.builder()
                .lectureId("LEC001")
                .categoryName(PopularLectureCategory.CORE_CULTURE)
                .build();
        PopularLectureDto dto2 = PopularLectureDto.builder()
                .lectureId("LEC002")
                .categoryName(PopularLectureCategory.COMMON_CULTURE)
                .build();

        Map<String, Integer> ranking = new HashMap<>();
        int startRank = 299; // MAX_POPULARITY_CONSIDERED = 300

        method.invoke(sut, List.of(dto1, dto2), new HashSet<>(), ranking, startRank);

        // 첫 번째만 추가되고 두 번째는 MAX 도달로 스킵되어야 함
        assertThat(ranking).containsKey("LEC001");
        assertThat(ranking).doesNotContainKey("LEC002");
    }

    @Test
    @DisplayName("shouldIncludePopularLecture: CORE_CULTURE는 항상 true")
    void shouldIncludePopularLecture_coreCulture() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "shouldIncludePopularLecture", PopularLectureCategory.class, Set.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(sut, PopularLectureCategory.CORE_CULTURE, new HashSet<>());

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldIncludePopularLecture: interestedCategories에 매칭되는 전공 카테고리는 true")
    void shouldIncludePopularLecture_matchesInterestedCategory() throws Exception {
        Method method = RecommendAfterTimetableService.class.getDeclaredMethod(
                "shouldIncludePopularLecture", PopularLectureCategory.class, Set.class);
        method.setAccessible(true);

        Set<GraduationCategory> interested = Set.of(GraduationCategory.PRIMARY_MANDATORY_MAJOR);

        boolean result = (boolean) method.invoke(sut, PopularLectureCategory.MANDATORY_MAJOR, interested);

        assertThat(result).isTrue();
    }

}
