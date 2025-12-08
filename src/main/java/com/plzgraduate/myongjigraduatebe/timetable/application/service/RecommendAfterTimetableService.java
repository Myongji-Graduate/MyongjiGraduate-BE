package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorLectureOfferingPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.BasicCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CoreCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.CommonCultureMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLectureOffering;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendAfterTimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.RequirementSnapshotQueryPort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.RecommendAfterTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
@Slf4j
public class RecommendAfterTimetableService implements RecommendAfterTimetableUseCase {

    private static final int MAX_PER_SEMESTER = 18;
    // 한 학기 target 학점 대비, 한 과목 정도 초과 허용(예: 2 남았는데 3학점 과목)
    private static final int MAX_SEMESTER_SLACK = 3;
    private static final int MAX_POPULARITY_CONSIDERED = 300;
    private static final String CHAPEL_CODE = "KMA02101";

    private final FindUserPort findUserPort;
    private final RemainingSemesterCalculator remainingSemesterCalculator;
    private final CreditTargetPolicy creditTargetPolicy;
    private final RemainingCreditsProvider remainingCreditsProvider;

    // 추가 주입
    private final RequirementSnapshotQueryPort requirementSnapshotQueryPort;
    private final TakenLectureQuery takenLectureQuery;
    private final FindLecturePort findLecturePort;
    private final RecommendedLectureExtractor recommendedLectureExtractor;
    private final MajorLectureOfferingPort majorLectureOfferingPort;
    private final PopularLecturePort popularLecturePort;
    private final MajorMembershipPort majorMembershipPort;
    private final BasicCultureMembershipPort basicCultureMembershipPort;
    private final CoreCultureMembershipPort coreCultureMembershipPort;
    private final CommonCultureMembershipPort commonCultureMembershipPort;

    private DetailRequirementContext buildDetailRequirementContext(Long userId, RequirementSnapshot snapshot, User user) {
        Map<DetailKey, DetailRequirement> requirements = new LinkedHashMap<>();
        Map<String, DetailKey> lectureIndex = new HashMap<>();

        Set<GraduationCategory> applicableCategories = getApplicableCategories(user);
        Map<GraduationCategory, RequirementSnapshot.Item> snapshotItems =
                (snapshot == null || snapshot.getItems() == null) ? Collections.emptyMap() : snapshot.getItems();

        for (GraduationCategory category : applicableCategories) {
            if (category == GraduationCategory.CHAPEL) continue;
            RequirementSnapshot.Item item = snapshotItems.get(category);
            if (item == null) continue;

            processCategoryRequirements(userId, category, item, requirements, lectureIndex);
        }

        return DetailRequirementContext.of(requirements, lectureIndex);
    }

    private void processCategoryRequirements(Long userId, GraduationCategory category,
                                             RequirementSnapshot.Item item,
                                             Map<DetailKey, DetailRequirement> requirements,
                                             Map<String, DetailKey> lectureIndex) {
        List<RecommendedLectureExtractor.DetailRecommendation> detailRecommendations =
                recommendedLectureExtractor.extractDetailRecommendations(userId, category);

        if (detailRecommendations.isEmpty()) {
            processCategoryWithoutDetails(category, item, userId, requirements, lectureIndex);
            return;
        }

        processDetailRecommendations(detailRecommendations, requirements, lectureIndex);
    }

    private void processCategoryWithoutDetails(GraduationCategory category, RequirementSnapshot.Item item,
                                               Long userId,
                                               Map<DetailKey, DetailRequirement> requirements,
                                               Map<String, DetailKey> lectureIndex) {
        List<String> haveTo = safeExtractRecommended(userId, category);
        int remaining = item.getRemainingCredit();
        if (remaining <= 0 || haveTo.isEmpty()) {
            return;
        }
        DetailRequirement requirement = DetailRequirement.of(category, category.getName(), remaining, haveTo);
        requirements.put(requirement.key(), requirement);
        requirement.haveToLectureIds().forEach(id -> lectureIndex.put(id, requirement.key()));
    }

    private void processDetailRecommendations(List<RecommendedLectureExtractor.DetailRecommendation> detailRecommendations,
                                             Map<DetailKey, DetailRequirement> requirements,
                                             Map<String, DetailKey> lectureIndex) {
        detailRecommendations.forEach(detail -> {
            int remaining = detail.getRemainingCredit();
            List<String> haveTo = detail.getHaveToLectureIds();
            if (remaining <= 0 || haveTo.isEmpty()) {
                return;
            }
            DetailRequirement requirement = DetailRequirement.of(
                    detail.getGraduationCategory(),
                    detail.getDetailCategoryName(),
                    remaining,
                    haveTo
            );
            requirements.put(requirement.key(), requirement);
            requirement.haveToLectureIds().forEach(id -> lectureIndex.put(id, requirement.key()));
        });
    }

    /**
     * 이번 학기 target 학점(cap) 기준 카테고리별 할당량 계산(비율 배분)
     * - 소수점 반올림으로 1학점 단위 분배
     * - 합계가 cap을 넘어가면 초과분을 많이 받은 카테고리부터 차감
     * - 남은 학점이 3학점 이하인 카테고리는 남은 학점만큼만 할당 (과도한 추천 방지)
     */
    private Map<DetailKey, Integer> makeSemesterQuota(int cap, Map<DetailKey, Integer> deficits) {
        Map<DetailKey, Integer> quota = new LinkedHashMap<>();
        if (cap <= 0 || deficits.isEmpty()) return quota;

        int totalDef = deficits.values().stream().mapToInt(Integer::intValue).sum();
        if (totalDef == 0) return quota;

        Map<DetailKey, Integer> smallDeficits = new LinkedHashMap<>();
        Map<DetailKey, Integer> largeDeficits = new LinkedHashMap<>();
        separateDeficitsBySize(deficits, smallDeficits, largeDeficits);

        int smallDefTotal = smallDeficits.values().stream().mapToInt(Integer::intValue).sum();
        int remainingCap = Math.max(0, cap - smallDefTotal);

        quota.putAll(smallDeficits);

        if (remainingCap > 0 && !largeDeficits.isEmpty()) {
            allocateLargeDeficits(largeDeficits, remainingCap, quota);
        }

        enforceQuotaLimits(quota, deficits);
        return quota;
    }

    private void separateDeficitsBySize(Map<DetailKey, Integer> deficits,
                                       Map<DetailKey, Integer> smallDeficits,
                                       Map<DetailKey, Integer> largeDeficits) {
        for (var e : deficits.entrySet()) {
            if (e.getValue() <= 3) {
                smallDeficits.put(e.getKey(), e.getValue());
            } else {
                largeDeficits.put(e.getKey(), e.getValue());
            }
        }
    }

    private void allocateLargeDeficits(Map<DetailKey, Integer> largeDeficits, int remainingCap,
                                      Map<DetailKey, Integer> quota) {
        int largeDefTotal = largeDeficits.values().stream().mapToInt(Integer::intValue).sum();
        if (largeDefTotal <= 0) return;

        Map<DetailKey, Double> ideal = new LinkedHashMap<>();
        int sum = allocateProportionally(largeDeficits, largeDefTotal, remainingCap, ideal, quota);

        if (sum != remainingCap) {
            adjustQuotaDifference(sum, remainingCap, ideal, quota);
        }
    }

    private int allocateProportionally(Map<DetailKey, Integer> largeDeficits, int largeDefTotal,
                                      int remainingCap, Map<DetailKey, Double> ideal,
                                      Map<DetailKey, Integer> quota) {
        int sum = 0;
        for (var e : largeDeficits.entrySet()) {
            double portion = (double) e.getValue() / largeDefTotal;
            double idealVal = portion * remainingCap;
            ideal.put(e.getKey(), idealVal);
            int alloc = (int) Math.round(idealVal);
            quota.put(e.getKey(), alloc);
            sum += alloc;
        }
        return sum;
    }

    private void adjustQuotaDifference(int sum, int remainingCap, Map<DetailKey, Double> ideal,
                                      Map<DetailKey, Integer> quota) {
        int gap = sum - remainingCap;
        List<DetailKey> order = new ArrayList<>(quota.keySet());
        order.sort((a, b) -> {
            double da = Math.abs(ideal.getOrDefault(a, 0.0) - quota.getOrDefault(a, 0));
            double db = Math.abs(ideal.getOrDefault(b, 0.0) - quota.getOrDefault(b, 0));
            return Double.compare(db, da);
        });
        int step = gap > 0 ? -1 : 1;
        int remain = Math.abs(gap);
        int idx = 0;
        while (remain > 0 && !order.isEmpty()) {
            DetailKey c = order.get(idx % order.size());
            int cur = quota.getOrDefault(c, 0) + step;
            if (cur >= 0) {
                quota.put(c, cur);
                remain--;
            }
            idx++;
        }
    }

    private void enforceQuotaLimits(Map<DetailKey, Integer> quota, Map<DetailKey, Integer> deficits) {
        quota.replaceAll((k, v) -> {
            int deficit = deficits.getOrDefault(k, 0);
            return Math.clamp(v, 0, deficit);
        });
    }

    /**
     * 카테고리 쿼터 대비 현재까지 뽑은 학점의 격차(underfill)를 기준으로
     * 다음에 노려야 할 카테고리를 하나 고른다.
     * - 아직 quota가 없는 카테고리는 스킵
     * - 전부 quota 달성시 Optional.empty()
     */
    private Optional<DetailKey> chooseNextDetail(Map<DetailKey, Integer> quota,
                                                 Map<DetailKey, Integer> pickedByDetail) {
        if (quota == null || quota.isEmpty()) return Optional.empty();
        DetailKey best = null;
        int bestGap = 0; // (quota - picked)가 가장 큰 카테고리
        for (var e : quota.entrySet()) {
            DetailKey key = e.getKey();
            int q = e.getValue();
            if (q <= 0) continue;
            int p = pickedByDetail.getOrDefault(key, 0);
            int gap = q - p;
            if (gap > bestGap) {
                bestGap = gap;
                best = key;
            }
        }
        return Optional.ofNullable(best);
    }

    /** 강의가 주어진 detail 카테고리에 속하는지 검사(미이수 기준 역매핑 사용) */
    private boolean lectureInDetailCategory(Lecture l, DetailKey detailKey, Map<String, DetailKey> detailKeyByLectureId) {
        if (l == null || detailKey == null) return false;
        DetailKey mapped = detailKeyByLectureId.get(l.getId());
        if (mapped == null) return false;
        return mapped.equals(detailKey);
    }

    private boolean hasRemainingDeficit(
            Lecture lecture,
            Map<String, DetailKey> detailKeyByLectureId,
            Map<DetailKey, Integer> deficits
    ) {
        if (lecture == null || detailKeyByLectureId == null || deficits == null) {
            return true;
        }
        DetailKey key = detailKeyByLectureId.get(lecture.getId());
        if (key == null) {
            return true;
        }
        Integer left = deficits.get(key);
        return left != null && left > 0;
    }

    @Override
    public RecommendAfterTimetableResponse build(Long userId) {
        User user = getUserOrThrow(userId);

        int remainingSemesters = remainingSemesterCalculator.from(user);
        RemainingSemesterCalculator.NextSemester start = remainingSemesterCalculator.nextSemester(user);

        OptionalInt remainingCreditsOpt = remainingCreditsProvider.get(user);
        List<Integer> creditPlan = remainingCreditsOpt.isPresent()
                ? creditTargetPolicy.plan(remainingCreditsOpt.getAsInt(), remainingSemesters)
                : null;

        RequirementSnapshot snapshot = requirementSnapshotQueryPort.getSnapshot(user, remainingSemesters);
        int chapelLeft = calcChapelLeft(snapshot);

        Set<String> takenCodes = takenLectureQuery.findAlreadyTakenLectureCodes(user);
        List<Lecture> availableLectures = buildAvailableLectures(takenCodes, chapelLeft);

        DetailRequirementContext detailRequirementContext = buildDetailRequirementContext(userId, snapshot, user);
        Map<String, DetailKey> detailKeyByLectureId = detailRequirementContext.lectureToDetailKey();
        Map<DetailKey, Integer> deficits = detailRequirementContext.remainingCredits();
        applyPopularityOrdering(user, availableLectures, detailKeyByLectureId);

        List<RecommendAfterTimetableResponse.SemesterBlock> semesters = planSemesters(
                new PlanningArgs(
                        remainingSemesters,
                        start,
                        creditPlan,
                        chapelLeft,
                        availableLectures,
                        detailRequirementContext,
                        detailKeyByLectureId,
                        deficits
                )
        );

        return RecommendAfterTimetableResponse.builder()
                .semestersLeft(remainingSemesters)
                .semesters(semesters)
                .build();
    }

    private User getUserOrThrow(Long userId) {
        return findUserPort.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private int calcChapelLeft(RequirementSnapshot snapshot) {
        if (snapshot == null || snapshot.getItems() == null) return 0;
        RequirementSnapshot.Item chapelItem = snapshot.getItems().get(GraduationCategory.CHAPEL);
        if (chapelItem == null) return 0;
        double total = chapelItem.getTotalCredit();
        double taken = chapelItem.getTakenCredit();
        double remainingCredit = Math.max(0.0, total - taken);
        return (int) Math.round(remainingCredit / ChapelResult.CHAPEL_CREDIT);
    }

    private List<Lecture> buildAvailableLectures(Set<String> takenCodes, int chapelLeft) {
        final int finalChapelLeft = chapelLeft;
        return findLecturePort.findAllLectures().stream()
                .filter(l -> l.getIsRevoked() == 0)
                .filter(l -> {
                    if (isChapel(l) && finalChapelLeft > 0) return true;
                    return !takenCodes.contains(l.getId());
                })
                .sorted(Comparator.comparing(Lecture::getId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void applyPopularityOrdering(User user,
                                         List<Lecture> availableLectures,
                                         Map<String, DetailKey> detailKeyByLectureId) {
        if (availableLectures == null || availableLectures.isEmpty()) return;
        Map<String, Integer> ranking = buildPopularityRanking(user, detailKeyByLectureId);
        if (ranking.isEmpty()) return;
        availableLectures.sort(Comparator
                .comparingInt((Lecture l) -> ranking.getOrDefault(l.getId(), Integer.MAX_VALUE))
                .thenComparing(Lecture::getId));
    }

    private Map<String, Integer> buildPopularityRanking(User user,
                                                        Map<String, DetailKey> detailKeyByLectureId) {
        if (popularLecturePort == null
                || detailKeyByLectureId == null
                || detailKeyByLectureId.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<GraduationCategory> interestedCategories = extractInterestedCategories(detailKeyByLectureId);
        if (interestedCategories.isEmpty()) {
            return Collections.emptyMap();
        }

        List<PopularLectureDto> popularLectures = loadPopularLectures();
        if (popularLectures == null || popularLectures.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Integer> ranking = new HashMap<>();
        int rank = 0;

        rank = addBasicCultureByMajor(user, interestedCategories, ranking, rank);
        addGlobalPopularLectures(popularLectures, interestedCategories, ranking, rank);

        return ranking;
    }

    private Set<GraduationCategory> extractInterestedCategories(Map<String, DetailKey> detailKeyByLectureId) {
        return detailKeyByLectureId.values().stream()
                .filter(Objects::nonNull)
                .map(DetailKey::getGraduationCategory)
                .collect(Collectors.toSet());
    }

    private List<PopularLectureDto> loadPopularLectures() {
        try {
            return popularLecturePort.getPopularLecturesByTotalCount();
        } catch (RuntimeException e) {
            log.warn("[RecommendAfter] Failed to load popular lectures: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private int addBasicCultureByMajor(User user, Set<GraduationCategory> interestedCategories,
                                      Map<String, Integer> ranking, int rank) {
        if (!shouldAddBasicCultureByMajor(user, interestedCategories)) {
            return rank;
        }

        try {
            List<PopularLectureDto> basicByMajor = loadBasicCultureByMajor(user);
            if (basicByMajor != null) {
                rank = processBasicCultureLectures(basicByMajor, ranking, rank);
            }
        } catch (RuntimeException e) {
            log.warn("[RecommendAfter] Failed to load basic-culture popular lectures by major: {}", e.getMessage());
        }
        return rank;
    }

    private boolean shouldAddBasicCultureByMajor(User user, Set<GraduationCategory> interestedCategories) {
        boolean needsBasic = interestedCategories.contains(GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE)
                || interestedCategories.contains(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE);
        return needsBasic && user != null && user.getPrimaryMajor() != null;
    }

    private List<PopularLectureDto> loadBasicCultureByMajor(User user) {
        return popularLecturePort.getLecturesByCategory(
                user.getPrimaryMajor(),
                user.getEntryYear(),
                PopularLectureCategory.BASIC_ACADEMICAL_CULTURE,
                MAX_POPULARITY_CONSIDERED,
                null
        );
    }

    private int processBasicCultureLectures(List<PopularLectureDto> basicByMajor,
                                            Map<String, Integer> ranking, int rank) {
        for (PopularLectureDto dto : basicByMajor) {
            if (dto == null || dto.getLectureId() == null) continue;
            if (ranking.containsKey(dto.getLectureId())) continue;
            ranking.put(dto.getLectureId(), rank++);
            if (rank >= MAX_POPULARITY_CONSIDERED) break;
        }
        return rank;
    }

    private int addGlobalPopularLectures(List<PopularLectureDto> popularLectures,
                                        Set<GraduationCategory> interestedCategories,
                                        Map<String, Integer> ranking, int rank) {
        for (PopularLectureDto dto : popularLectures) {
            if (dto == null || dto.getLectureId() == null) continue;
            if (ranking.containsKey(dto.getLectureId())) continue;

            PopularLectureCategory popularCategory = dto.getCategoryName();
            if (popularCategory == null) continue;
            if (popularCategory == PopularLectureCategory.BASIC_ACADEMICAL_CULTURE) continue;

            if (!shouldIncludePopularLecture(popularCategory, interestedCategories)) {
                continue;
            }

            ranking.put(dto.getLectureId(), rank++);
            if (rank >= MAX_POPULARITY_CONSIDERED) break;
        }
        return rank;
    }

    private boolean shouldIncludePopularLecture(PopularLectureCategory popularCategory,
                                               Set<GraduationCategory> interestedCategories) {
        boolean isCultureCategory = popularCategory == PopularLectureCategory.CORE_CULTURE
                || popularCategory == PopularLectureCategory.COMMON_CULTURE
                || popularCategory == PopularLectureCategory.NORMAL_CULTURE;

        return isCultureCategory || matchesInterestedCategory(popularCategory, interestedCategories);
    }

    private boolean matchesInterestedCategory(PopularLectureCategory popularCategory,
                                              Set<GraduationCategory> interestedCategories) {
        for (GraduationCategory graduationCategory : interestedCategories) {
            if (popularCategoryMatches(popularCategory, graduationCategory)) {
                return true;
            }
        }
        return false;
    }

    private boolean popularCategoryMatches(PopularLectureCategory popularCategory,
                                           GraduationCategory graduationCategory) {
        return switch (popularCategory) {
            case MANDATORY_MAJOR -> graduationCategory == GraduationCategory.PRIMARY_MANDATORY_MAJOR
                    || graduationCategory == GraduationCategory.DUAL_MANDATORY_MAJOR;
            case ELECTIVE_MAJOR -> graduationCategory == GraduationCategory.PRIMARY_ELECTIVE_MAJOR
                    || graduationCategory == GraduationCategory.DUAL_ELECTIVE_MAJOR
                    || graduationCategory == GraduationCategory.SUB_MAJOR;
            case BASIC_ACADEMICAL_CULTURE -> graduationCategory == GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE
                    || graduationCategory == GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE;
            case CORE_CULTURE -> graduationCategory == GraduationCategory.CORE_CULTURE;
            case COMMON_CULTURE -> graduationCategory == GraduationCategory.COMMON_CULTURE;
            case NORMAL_CULTURE -> graduationCategory == GraduationCategory.NORMAL_CULTURE
                    || graduationCategory == GraduationCategory.FREE_ELECTIVE;
            case ALL -> true;
        };
    }

    /**
     * 학생 유형에 포함된 GraduationCategory Set 반환
     */
    private Set<GraduationCategory> getApplicableCategories(User user) {
        StudentCategory studentCategory = user.getStudentCategory();
        if (studentCategory == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(studentCategory.getIncludedGraduationCategories());
    }

    private List<String> safeExtractRecommended(Long userId, GraduationCategory cat) {
        try {
            List<String> ids = recommendedLectureExtractor.extractRecommendedLectureIds(userId, cat);
            return (ids == null) ? List.of() : ids;
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("UNFITTED_GRADUATION_CATEGORY")) return List.of();
            throw e;
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("No calculate detail graduation case found")) return List.of();
            throw e;
        }
    }

    private List<RecommendAfterTimetableResponse.SemesterBlock> planSemesters(PlanningArgs a) {
        // 준비: 미이수 과목(채플 제외)과 필수 과목 리스트
        LinkedHashSet<String> haveToSet = toHaveToSet(a.detailRequirementContext);
        List<Lecture> mustLectures = filterMustLectures(a.availableLectures, haveToSet);

        Map<String, Optional<MajorLectureOffering>> offeringCache = new HashMap<>();
        Set<String> recommendedAcross = new HashSet<>();
        List<RecommendAfterTimetableResponse.SemesterBlock> semesters = new ArrayList<>(a.remainingSemesters);

        int grade = a.start.getGrade();
        int semester = a.start.getSemester();
        int chapelLeft = a.chapelLeftInit;

        for (int i = 0; i < a.remainingSemesters; i++) {
            String label = grade + "-" + semester;
            int target = (a.creditPlan != null)
                    ? a.creditPlan.get(i)
                    : creditTargetPolicy.targetForIndex(i, a.remainingSemesters);
            target = Math.min(target, MAX_PER_SEMESTER);

            SemesterFillingResult fill = pickLecturesForSemester(
                    target,
                    chapelLeft,
                    grade, semester,
                    mustLectures,
                    a.availableLectures,
                    recommendedAcross,
                    offeringCache,
                    a.detailKeyByLectureId,
                    a.deficits
            );

            semesters.add(RecommendAfterTimetableResponse.SemesterBlock.builder()
                    .label(label)
                    .creditTarget(fill.creditTarget)
                    .lectures(fill.picks)
                    .build());

            chapelLeft = fill.chapelLeftAfter;
            int[] next = advanceSemester(grade, semester);
            grade = next[0];
            semester = next[1];
        }
        return semesters;
    }

    private static final class SemesterFillingResult {
        final int creditTarget;
        final int chapelLeftAfter;
        final List<RecommendAfterTimetableResponse.LectureItem> picks;
        SemesterFillingResult(int creditTarget, int chapelLeftAfter,
                              List<RecommendAfterTimetableResponse.LectureItem> picks) {
            this.creditTarget = creditTarget;
            this.chapelLeftAfter = chapelLeftAfter;
            this.picks = picks;
        }
    }

    private SemesterFillingResult pickLecturesForSemester(
            int target,
            int chapelLeft,
            int grade, int semester,
            List<Lecture> mustLectures,
            List<Lecture> availableLectures,
            Set<String> recommendedAcross,
            Map<String, Optional<MajorLectureOffering>> offeringCache,
            Map<String, DetailKey> detailKeyByLectureId,
            Map<DetailKey, Integer> deficits
    ) {
        List<RecommendAfterTimetableResponse.LectureItem> picks = new ArrayList<>();
        int cur = 0;

        // 1) 채플 우선 1과목
        if (chapelLeft > 0) {
            int chapelCredit = placeChapelIfAny(availableLectures, detailKeyByLectureId, picks);
            if (chapelCredit >= 0) {
                cur += chapelCredit;
                chapelLeft--;
            }
        }

        // 2) 쿼터 계산 및 채우기
        Map<DetailKey, Integer> semesterQuota = makeSemesterQuota(target - cur, deficits);
        Map<DetailKey, Integer> pickedByDetail = new LinkedHashMap<>();

        while (cur < target) {
            int remain = target - cur;
            Optional<DetailKey> targetDetailOpt = chooseNextDetail(semesterQuota, pickedByDetail);

            PickContext ctx = new PickContext(
                    recommendedAcross, remain, new SemesterInfo(grade, semester),
                    offeringCache, targetDetailOpt, detailKeyByLectureId, deficits
            );

            Lecture next = chooseNextLecture(mustLectures, availableLectures, ctx);
            if (next == null) break;

            String catName = resolveCategory(next.getId(), detailKeyByLectureId);
            picks.add(toDto(next, catName));
            recommendedAcross.add(next.getId());

            int c = safeCredit(next);
            cur += c;
            applyPick(next, c, detailKeyByLectureId, pickedByDetail, deficits);

            if (cur < target) {
                semesterQuota = makeSemesterQuota(target - cur, deficits);
            }
        }
        return new SemesterFillingResult(target, chapelLeft, picks);
    }

    private int[] advanceSemester(int grade, int semester) {
        if (semester == 1) return new int[]{grade, 2};
        return new int[]{grade + 1, 1};
    }

    // ====== 새 헬퍼들 ======
    /**
     * Pick-time context to reduce parameter count for chooser methods.
     */
    private record SemesterInfo(int grade, int semester) {}

    private static final class PickContext {
        final Set<String> recommendedAcross;
        final int remain;
        final SemesterInfo semesterInfo;
        final Map<String, Optional<MajorLectureOffering>> offeringCache;
        final Optional<DetailKey> targetDetailOpt;
        final Map<String, DetailKey> detailKeyByLectureId;
        final Map<DetailKey, Integer> deficits;

        PickContext(
                Set<String> recommendedAcross,
                int remain,
                SemesterInfo semesterInfo,
                Map<String, Optional<MajorLectureOffering>> offeringCache,
                Optional<DetailKey> targetDetailOpt,
                Map<String, DetailKey> detailKeyByLectureId,
                Map<DetailKey, Integer> deficits
        ) {
            this.recommendedAcross = recommendedAcross;
            this.remain = remain;
            this.semesterInfo = semesterInfo;
            this.offeringCache = offeringCache;
            this.targetDetailOpt = targetDetailOpt;
            this.detailKeyByLectureId = detailKeyByLectureId;
            this.deficits = deficits;
        }
    }

    private static final class PlanningArgs {
        final int remainingSemesters;
        final RemainingSemesterCalculator.NextSemester start;
        final List<Integer> creditPlan;
        final int chapelLeftInit;
        final List<Lecture> availableLectures;
        final DetailRequirementContext detailRequirementContext;
        final Map<String, DetailKey> detailKeyByLectureId;
        final Map<DetailKey, Integer> deficits;

        PlanningArgs(int remainingSemesters,
                     RemainingSemesterCalculator.NextSemester start,
                     List<Integer> creditPlan,
                     int chapelLeftInit,
                     List<Lecture> availableLectures,
                     DetailRequirementContext detailRequirementContext,
                     Map<String, DetailKey> detailKeyByLectureId,
                     Map<DetailKey, Integer> deficits) {
            this.remainingSemesters = remainingSemesters;
            this.start = start;
            this.creditPlan = creditPlan;
            this.chapelLeftInit = chapelLeftInit;
            this.availableLectures = availableLectures;
            this.detailRequirementContext = detailRequirementContext;
            this.detailKeyByLectureId = detailKeyByLectureId;
            this.deficits = deficits;
        }
    }

    private static final class DetailRequirementContext {
        private final Map<DetailKey, DetailRequirement> requirements;
        private final Map<String, DetailKey> lectureToDetailKey;

        private DetailRequirementContext(Map<DetailKey, DetailRequirement> requirements,
                                         Map<String, DetailKey> lectureToDetailKey) {
            this.requirements = Collections.unmodifiableMap(new LinkedHashMap<>(requirements));
            this.lectureToDetailKey = Collections.unmodifiableMap(new HashMap<>(lectureToDetailKey));
        }

        static DetailRequirementContext of(Map<DetailKey, DetailRequirement> requirements,
                                           Map<String, DetailKey> lectureToDetailKey) {
            return new DetailRequirementContext(requirements, lectureToDetailKey);
        }

        Map<DetailKey, Integer> remainingCredits() {
            Map<DetailKey, Integer> copy = new LinkedHashMap<>();
            requirements.values()
                    .forEach(req -> {
                        if (req.remainingCredit() > 0) {
                            copy.put(req.key(), req.remainingCredit());
                        }
                    });
            return copy;
        }

        Map<String, DetailKey> lectureToDetailKey() {
            return new HashMap<>(lectureToDetailKey);
        }

        Collection<List<String>> haveToLectureLists() {
            return requirements.values().stream()
                    .map(DetailRequirement::haveToLectureIds)
                    .toList();
        }
    }

    private record DetailRequirement(
            DetailKey key,
            int remainingCredit,
            List<String> haveToLectureIds
    ) {
        static DetailRequirement of(GraduationCategory category, String detailName,
                                    int remainingCredit, List<String> haveToLectureIds) {
            DetailKey key = DetailKey.of(category, detailName);
            List<String> safeList = haveToLectureIds == null ? List.of() : List.copyOf(haveToLectureIds);
            return new DetailRequirement(key, remainingCredit, safeList);
        }
    }

    private static final class DetailKey {
        private final GraduationCategory graduationCategory;
        private final String detailName;

        private DetailKey(GraduationCategory graduationCategory, String detailName) {
            this.graduationCategory = graduationCategory;
            this.detailName = detailName == null ? graduationCategory.getName() : detailName;
        }

        static DetailKey of(GraduationCategory graduationCategory, String detailName) {
            if (graduationCategory == null) {
                throw new IllegalArgumentException("graduationCategory must not be null");
            }
            return new DetailKey(graduationCategory, detailName);
        }

        GraduationCategory getGraduationCategory() {
            return graduationCategory;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DetailKey detailKey)) return false;
            return graduationCategory == detailKey.graduationCategory &&
                    Objects.equals(detailName, detailKey.detailName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(graduationCategory, detailName);
        }

        @Override
        public String toString() {
            return graduationCategory + ":" + detailName;
        }
    }

    /** haveToByCategory에서 채플을 제외한 미이수 과목 ID를 LinkedHashSet으로 수집 */
    private LinkedHashSet<String> toHaveToSet(DetailRequirementContext detailRequirementContext) {
        if (detailRequirementContext == null) {
            return new LinkedHashSet<>();
        }
        return detailRequirementContext.haveToLectureLists().stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .filter(id -> !CHAPEL_CODE.equalsIgnoreCase(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** availableLectures에서 haveToSet에 해당하는 과목만 추려 정렬 */
    private List<Lecture> filterMustLectures(List<Lecture> availableLectures, Set<String> haveToSet) {
        return availableLectures.stream()
                .filter(l -> haveToSet.contains(l.getId()))
                .sorted(Comparator.comparing(Lecture::getId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** 가능하면 채플 1과목을 picks에 추가하고, 추가된 학점을 반환 */
    private int placeChapelIfAny(
            List<Lecture> availableLectures,
            Map<String, DetailKey> detailKeyByLectureId,
            List<RecommendAfterTimetableResponse.LectureItem> picks
    ) {
        Lecture chapel = findFirst(availableLectures, this::isChapel);
        if (chapel == null) return -1;
        String cat = resolveCategory(chapel.getId(), detailKeyByLectureId);
        picks.add(toDto(chapel, cat));
        return safeCredit(chapel);
    }

    /** 다음에 고를 과목 하나 선택(없으면 null) */
    private Lecture chooseNextLecture(
            List<Lecture> mustLectures,
            List<Lecture> availableLectures,
            PickContext ctx
    ) {
        // 기본 필터: 이미 추천한 과목 제외, 학점 제한(전공은 최대 +1 허용), 개설학기 체크
        Predicate<Lecture> base = l ->
                !ctx.recommendedAcross.contains(l.getId()) &&
                        safeCredit(l) > 0 &&
                        canTakeCreditWithSlack(l, ctx.remain) &&
                        isOfferedNow(l, ctx.semesterInfo.grade(), ctx.semesterInfo.semester(), ctx.offeringCache, ctx.detailKeyByLectureId);

        // ---------------------------------------------------------------------
        // 1) 타겟 detail 카테고리 우선 (필수 → 일반)
        // ---------------------------------------------------------------------
        if (ctx.targetDetailOpt.isPresent()) {
            DetailKey targetDetail = ctx.targetDetailOpt.get();
            Predicate<Lecture> inDetail =
                    l -> lectureInDetailCategory(l, targetDetail, ctx.detailKeyByLectureId);

            // (a) 타겟 detail + 필수(must)
            Lecture next = pickAndRemoveFirst(mustLectures, base.and(inDetail));
            if (next != null) return next;

            // (b) 타겟 detail + 일반
            next = pickAndRemoveFirst(
                    availableLectures,
                    base.and(inDetail).and(l -> !isChapel(l))
            );
            if (next != null) return next;
        }

        // ---------------------------------------------------------------------
        // 2) detail 카테고리 무관 필수(must) → 단, deficits를 줄이는 경우만
        // ---------------------------------------------------------------------
        Lecture next = pickAndRemoveFirst(
                mustLectures,
                base.and(l -> hasRemainingDeficit(l, ctx.detailKeyByLectureId, ctx.deficits))
        );
        if (next != null) return next;

        // ---------------------------------------------------------------------
        // 3) deficits가 남아 있으면 → deficits 줄일 수 있는 과목만 일반 pool에서 선택
        //    - NORMAL_CULTURE / FREE_ELECTIVE를 채울 때는 "진짜 일반교양(NORMAL_CULTURE)"을 우선 사용
        // ---------------------------------------------------------------------
        if (!ctx.deficits.isEmpty()) {
            // (a) NORMAL_CULTURE / FREE_ELECTIVE 부족이면 먼저 NORMAL_CULTURE 과목만 시도
            if (needsPureNormalCultureFirst(ctx.deficits)) {
                next = pickAndRemoveFirst(
                        availableLectures,
                        base.and(l -> !isChapel(l))
                                .and(l -> hasRemainingDeficit(l, ctx.detailKeyByLectureId, ctx.deficits))
                                .and(l -> isNormalCultureLecture(l, ctx.detailKeyByLectureId))
                );
                if (next != null) return next;
            }

            // (b) 그 외 또는 위에서 못 찾았으면 기존 로직대로 deficits 줄일 수 있는 아무 과목
            next = pickAndRemoveFirst(
                    availableLectures,
                    base.and(l -> !isChapel(l))
                            .and(l -> hasRemainingDeficit(l, ctx.detailKeyByLectureId, ctx.deficits))
            );
            if (next != null) return next;
        }

        // ---------------------------------------------------------------------
        // 4) deficits를 모두 채운 이후 → 전공/기초/핵심/공통을 제외한 "순수 일반교양"만으로 채움
        // ---------------------------------------------------------------------
        return pickAndRemoveFirst(
                availableLectures,
                base.and(l -> !isChapel(l))
                        .and(l -> isNormalCultureLecture(l, ctx.detailKeyByLectureId))
        );
    }

    /** 선정된 과목의 학점/카테고리를 반영하여 집계치 갱신 */
    private void applyPick(
            Lecture picked,
            int credit,
            Map<String, DetailKey> detailKeyByLectureId,
            Map<DetailKey, Integer> pickedByDetail,
            Map<DetailKey, Integer> deficits
    ) {
        DetailKey mappedDetail = detailKeyByLectureId.get(picked.getId());
        if (mappedDetail == null || mappedDetail.getGraduationCategory() == GraduationCategory.CHAPEL) return;

        pickedByDetail.merge(mappedDetail, credit, Integer::sum);
        deficits.merge(mappedDetail, -credit, Integer::sum);
        Integer left = deficits.get(mappedDetail);
        if (left != null && left <= 0) {
            deficits.remove(mappedDetail);
        }
    }

    /** 조건에 맞는 첫 과목을 찾아 리스트에서 제거 후 반환 */
    private Lecture pickAndRemoveFirst(List<Lecture> pool, Predicate<Lecture> cond) {
        for (int i = 0; i < pool.size(); i++) {
            Lecture l = pool.get(i);
            if (cond.test(l)) {
                pool.remove(i);
                return l;
            }
        }
        return null;
    }

    /** 조건에 맞는 첫 과목을 반환(풀에서 제거하지 않음) */
    private Lecture findFirst(List<Lecture> pool, Predicate<Lecture> cond) {
        for (Lecture l : pool) {
            if (cond.test(l)) return l;
        }
        return null;
    }

    /** 채플 판정: 오직 코드 "KMA02101"만 채플로 간주 */
    private boolean isChapel(Lecture l) {
        String code = l.getId();
        return CHAPEL_CODE.equalsIgnoreCase(code);
    }

    /** NORMAL_CULTURE / FREE_ELECTIVE deficit이 있는지 여부 */
    private boolean needsPureNormalCultureFirst(Map<DetailKey, Integer> deficits) {
        if (deficits == null || deficits.isEmpty()) return false;
        for (Map.Entry<DetailKey, Integer> entry : deficits.entrySet()) {
            GraduationCategory cat = entry.getKey().getGraduationCategory();
            if (cat == GraduationCategory.NORMAL_CULTURE || cat == GraduationCategory.FREE_ELECTIVE) {
                Integer left = entry.getValue();
                if (left != null && left > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 이 강의가 "진짜 일반교양(NORMAL_CULTURE)" 카테고리인지 여부
     *  - 조건:
     *    1) GraduationCategory.NORMAL_CULTURE 이고
     *    2) major / basic / core / common 어떤 테이블에도 속하지 않는 강의
     *  - 단, detailKey 매핑이 없는 경우에도 2)를 만족하면 순수 일반교양으로 인정
     */
    private boolean isNormalCultureLecture(Lecture lecture, Map<String, DetailKey> detailKeyByLectureId) {
        if (lecture == null) return false;
        String id = lecture.getId();
        if (id == null) return false;

        // 1) 어떤 전공/기초/핵심/공통 테이블에도 속하지 않는지 확인
        boolean ownedByStructuredCategory =
                (majorMembershipPort != null && majorMembershipPort.isMajorLecture(id))
                        || (basicCultureMembershipPort != null && basicCultureMembershipPort.isBasicLecture(id))
                        || (coreCultureMembershipPort != null && coreCultureMembershipPort.isCoreLecture(id))
                        || (commonCultureMembershipPort != null && commonCultureMembershipPort.isCommonLecture(id));
        if (ownedByStructuredCategory) {
            return false;
        }

        if (detailKeyByLectureId == null) {
            // 매핑 정보가 없으면, 위 조건 하나만으로 순수 일반교양으로 인정
            return true;
        }

        DetailKey key = detailKeyByLectureId.get(id);
        if (key == null) {
            // GraduationCategory 매핑이 없어도, 전공/기초/핵심/공통에 속하지 않으면 순수 일반교양
            return true;
        }
        return key.getGraduationCategory() == GraduationCategory.NORMAL_CULTURE;
    }

    /** NPE 방지용 크레딧 */
    private int safeCredit(Lecture l) {
        return l.getCredit();
    }

    /**
     * 남은 학점(remain) 대비 이 과목을 이번 학기에 배치할 수 있는지 여부.
     * - 기본: safeCredit(l) <= remain
     * - 단, 한 과목 정도는 초과를 허용하기 위해 remain + MAX_SEMESTER_SLACK 까지 허용
     *   (예: remain=2, 3학점 과목 허용 → 학기 target 보다 1학점 초과)
     */
    private boolean canTakeCreditWithSlack(
            Lecture lecture,
            int remain
    ) {
        int credit = safeCredit(lecture);
        if (credit <= 0) return false;
        return credit <= remain + MAX_SEMESTER_SLACK;
    }

    /** 카테고리 문자열 해석: 있으면 Enum 이름, 채플이면 CHAPEL, 없으면 '일반교양' */
    private String resolveCategory(String lectureId, Map<String, DetailKey> detailKeyByLectureId) {
        if (lectureId == null) return "일반교양";
        DetailKey key = detailKeyByLectureId.get(lectureId);
        if (key != null) return key.getGraduationCategory().getName();
        if (CHAPEL_CODE.equalsIgnoreCase(lectureId)) return GraduationCategory.CHAPEL.getName();
        return "일반교양";
    }

    private RecommendAfterTimetableResponse.LectureItem toDto(Lecture l, String category) {
        return RecommendAfterTimetableResponse.LectureItem.builder()
                .id(l.getId())
                .name(l.getName())
                .credit(safeCredit(l))
                .category(category)
                .build();
    }

    //
    public interface RemainingCreditsProvider {
        OptionalInt get(User user);
    }

    @Component
    public static class DefaultRemainingCreditsProvider implements RemainingCreditsProvider {
        @Override
        public OptionalInt get(User user) {
            int total = user.getTotalCredit();
            double taken = user.getTakenCredit();

            double remainingDouble = Math.max(0.0, total - taken);
            int remainingInt = (int) Math.ceil(remainingDouble);

            return OptionalInt.of(remainingInt);
        }
    }

    @Component
    public static class CreditTargetPolicy {
        public int targetForIndex(int idx, int total) {
            if (idx == total - 1) return 6;      // 마지막 학기 완화
            return 16;                           // 기본은 16 (너 규칙에 맞게 조절)
        }
        public List<Integer> plan(int totalCreditsLeft, int semesters) {
            if (semesters <= 0) return Collections.emptyList();
            int cap = MAX_PER_SEMESTER;
            // 평균 분배하되 마지막 학기 완화(최소 6), 각 학기 18 cap
            int base = Math.max(6, (int)Math.ceil(totalCreditsLeft / (double)semesters));
            base = Math.min(base, cap);
            List<Integer> plan = new ArrayList<>(semesters);
            for (int i = 0; i < semesters - 1; i++) plan.add(base);
            int used = base * (semesters - 1);
            int last = Math.max(6, totalCreditsLeft - used);
            plan.add(Math.min(last, cap));
            return plan;
        }
    }
    /** 현재 학년/학기에 수강 가능한 전공 개설 정보인지 확인 (전공 정보가 없으면 통과)
     *  - DB N+1 방지를 위해 요청 스코프 캐시(Map) 사용
     */
    private boolean isOfferedNow(
            Lecture l, int grade, int semester,
            Map<String, Optional<MajorLectureOffering>> cache,
            Map<String, DetailKey> detailKeyByLectureId
    ) {
        if (l == null || l.getId() == null) return true; // 방어
        // 채플은 전공 개설 정보와 무관
        if (isChapel(l)) return true;

        DetailKey detailKey = detailKeyByLectureId.get(l.getId());
        if (!requiresMajorOfferingCheck(detailKey)) {
            return true;
        }

        Optional<MajorLectureOffering> offeringOpt =
                cache.computeIfAbsent(l.getId(), majorLectureOfferingPort::findByLectureId);

        // 전공 매핑이 없거나(=전공 과목인데 레코드가 없을 때) 유효하지 않으면 추천 제외
        if (offeringOpt.isEmpty()) {
            return false;
        }

        MajorLectureOffering offering = offeringOpt.get();

        // grade / offered_semester 등 내부 필드가 null 이면 안전하게 제외
        return safeMatches(offering, grade, semester);
    }

    /** MajorLectureOffering 내부 필드가 null 이어도 NPE 없이 false 로 처리 */
    private boolean safeMatches(MajorLectureOffering offering, int grade, int semester) {
        try {
            return offering.matchesGrade(grade) && offering.isOfferedInSemester(semester);
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean requiresMajorOfferingCheck(DetailKey detailKey) {
        if (detailKey == null) {
            return false;
        }
        GraduationCategory category = detailKey.getGraduationCategory();
        return category == GraduationCategory.PRIMARY_MANDATORY_MAJOR
                || category == GraduationCategory.PRIMARY_ELECTIVE_MAJOR
                || category == GraduationCategory.DUAL_MANDATORY_MAJOR
                || category == GraduationCategory.DUAL_ELECTIVE_MAJOR
                || category == GraduationCategory.SUB_MAJOR;
    }
}
