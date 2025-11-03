package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.MajorLectureOfferingPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLectureOffering;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendAfterTimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.RequirementSnapshotQueryPort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.RecommendAfterTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class RecommendAfterTimetableService implements RecommendAfterTimetableUseCase {

    private static final int MAX_PER_SEMESTER = 18;
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

    /**
     * 카테고리별 남은 크레딧(스냅샷 기반) 계산: total - taken (0 미만이면 0)
     * - CHAPEL은 별도 처리하므로 제외
     */
    private Map<GraduationCategory, Integer> computeDeficits(RequirementSnapshot snapshot) {
        Map<GraduationCategory, Integer> deficits = new EnumMap<>(GraduationCategory.class);
        if (snapshot == null || snapshot.getItems() == null) return deficits;

        snapshot.getItems().forEach((GraduationCategory cat, RequirementSnapshot.Item item) -> {
            if (cat == null || cat == GraduationCategory.CHAPEL || item == null) return;

            int total = item.getTotalCredit();   // primitive int
            int taken = item.getTakenCredit();   // primitive int
            int left  = Math.max(0, total - taken);

            if (left > 0) deficits.put(cat, left);
        });
        return deficits;
    }

    /**
     * 이번 학기 target 학점(cap) 기준 카테고리별 할당량 계산(비율 배분)
     * - 소수점 반올림으로 1학점 단위 분배
     * - 합계가 cap을 넘어가면 초과분을 많이 받은 카테고리부터 차감
     */
    private Map<GraduationCategory, Integer> makeSemesterQuota(int cap, Map<GraduationCategory, Integer> deficits) {
        Map<GraduationCategory, Integer> quota = new EnumMap<>(GraduationCategory.class);
        if (cap <= 0 || deficits.isEmpty()) return quota;

        int totalDef = deficits.values().stream().mapToInt(Integer::intValue).sum();
        if (totalDef == 0) return quota;

        // 1차 배분(반올림)
        int sum = 0;
        Map<GraduationCategory, Double> ideal = new EnumMap<>(GraduationCategory.class);
        for (var e : deficits.entrySet()) {
            double portion = (double) e.getValue() / totalDef;
            double idealVal = portion * cap;
            ideal.put(e.getKey(), idealVal);
            int alloc = (int) Math.round(idealVal);
            quota.put(e.getKey(), alloc);
            sum += alloc;
        }
        // 2차 보정: 합계 != cap이면 보정
        if (sum != cap) {
            // gap > 0 이면 초과, < 0 이면 부족
            int gap = sum - cap;
            // 이상치에 가까운(=이상적 비중에서 많이 멀어진) 카테고리부터 조정
            // 초과면 많이 받은 카테고리부터 -1, 부족이면 적게 받은 카테고리부터 +1
            // (ideal - quota)의 절댓값 기준 정렬
            List<GraduationCategory> order = new ArrayList<>(quota.keySet());
            order.sort((a, b) -> {
                double da = Math.abs(ideal.getOrDefault(a, 0.0) - quota.getOrDefault(a, 0));
                double db = Math.abs(ideal.getOrDefault(b, 0.0) - quota.getOrDefault(b, 0));
                return Double.compare(db, da); // desc
            });
            int step = gap > 0 ? -1 : 1;
            int remain = Math.abs(gap);
            int idx = 0;
            while (remain > 0 && !order.isEmpty()) {
                GraduationCategory c = order.get(idx % order.size());
                int cur = quota.getOrDefault(c, 0) + step;
                if (cur >= 0) {
                    quota.put(c, cur);
                    remain--;
                }
                idx++;
            }
        }
        // 음수 방지
        quota.replaceAll((k, v) -> Math.max(0, v));
        return quota;
    }

    /**
     * 카테고리 쿼터 대비 현재까지 뽑은 학점의 격차(underfill)를 기준으로
     * 다음에 노려야 할 카테고리를 하나 고른다.
     * - 아직 quota가 없는 카테고리는 스킵
     * - 전부 quota 달성시 Optional.empty()
     */
    private Optional<GraduationCategory> chooseNextCategory(Map<GraduationCategory, Integer> quota,
                                                           Map<GraduationCategory, Integer> pickedByCat) {
        if (quota == null || quota.isEmpty()) return Optional.empty();
        GraduationCategory best = null;
        int bestGap = 0; // (quota - picked)가 가장 큰 카테고리
        for (var e : quota.entrySet()) {
            GraduationCategory cat = e.getKey();
            int q = e.getValue();
            if (q <= 0) continue;
            int p = pickedByCat.getOrDefault(cat, 0);
            int gap = q - p;
            if (gap > bestGap) {
                bestGap = gap;
                best = cat;
            }
        }
        return Optional.ofNullable(best);
    }

    /** 강의가 주어진 카테고리에 속하는지 검사(미이수 기준 역매핑 사용) */
    private boolean lectureInCategory(Lecture l, GraduationCategory cat, Map<String, GraduationCategory> categoryByLectureId) {
        if (l == null || cat == null) return false;
        GraduationCategory mapped = categoryByLectureId.get(l.getId());
        if (mapped == null) return false;
        return mapped == cat;
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

        Map<GraduationCategory, List<String>> haveToByCategory = buildHaveToByCategory(userId, snapshot);
        Map<String, GraduationCategory> categoryByLectureId = reverseIndex(haveToByCategory);
        Map<GraduationCategory, Integer> deficits = computeDeficits(snapshot);

        List<RecommendAfterTimetableResponse.SemesterBlock> semesters = planSemesters(
                new PlanningArgs(
                        remainingSemesters,
                        start,
                        creditPlan,
                        chapelLeft,
                        availableLectures,
                        haveToByCategory,
                        categoryByLectureId,
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
        int totalI = chapelItem.getTotalCredit();
        int takenI = chapelItem.getTakenCredit();
        return Math.max(0, totalI - takenI);
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

    private Map<GraduationCategory, List<String>> buildHaveToByCategory(Long userId, RequirementSnapshot snapshot) {
        Set<GraduationCategory> applicable = (snapshot == null || snapshot.getItems() == null)
                ? Collections.emptySet()
                : snapshot.getItems().keySet();

        return applicable.stream().collect(Collectors.toMap(
                cat -> cat,
                cat -> safeExtractRecommended(userId, cat)
        ));
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

    private Map<String, GraduationCategory> reverseIndex(Map<GraduationCategory, List<String>> haveToByCategory) {
        Map<String, GraduationCategory> map = new HashMap<>();
        haveToByCategory.forEach((cat, ids) -> {
            if (ids != null) for (String id : ids) if (id != null) map.put(id, cat);
        });
        return map;
    }

    private List<RecommendAfterTimetableResponse.SemesterBlock> planSemesters(PlanningArgs a) {
        // 준비: 미이수 과목(채플 제외)과 필수 과목 리스트
        LinkedHashSet<String> haveToSet = toHaveToSet(a.haveToByCategory);
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
                    a.categoryByLectureId,
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
            Map<String, GraduationCategory> categoryByLectureId,
            Map<GraduationCategory, Integer> deficits
    ) {
        List<RecommendAfterTimetableResponse.LectureItem> picks = new ArrayList<>();
        int cur = 0;

        // 1) 채플 우선 1과목
        if (chapelLeft > 0) {
            cur += placeChapelIfAny(availableLectures, categoryByLectureId, picks);
            if (cur > 0) chapelLeft--;
        }

        // 2) 쿼터 계산 및 채우기
        Map<GraduationCategory, Integer> semesterQuota = makeSemesterQuota(target - cur, deficits);
        Map<GraduationCategory, Integer> pickedByCat = new EnumMap<>(GraduationCategory.class);

        while (cur < target) {
            int remain = target - cur;
            Optional<GraduationCategory> targetCatOpt = chooseNextCategory(semesterQuota, pickedByCat);

            PickContext ctx = new PickContext(
                    recommendedAcross, remain, grade, semester,
                    offeringCache, targetCatOpt, categoryByLectureId
            );

            Lecture next = chooseNextLecture(mustLectures, availableLectures, ctx);
            if (next == null) break;

            String catName = resolveCategory(next.getId(), categoryByLectureId);
            picks.add(toDto(next, catName));
            recommendedAcross.add(next.getId());

            int c = safeCredit(next);
            cur += c;
            applyPick(next, c, categoryByLectureId, pickedByCat, deficits);

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
    private static final class PickContext {
        final Set<String> recommendedAcross;
        final int remain;
        final int curGrade;
        final int curSemester;
        final Map<String, Optional<MajorLectureOffering>> offeringCache;
        final Optional<GraduationCategory> targetCatOpt;
        final Map<String, GraduationCategory> categoryByLectureId;

        PickContext(
                Set<String> recommendedAcross,
                int remain,
                int curGrade,
                int curSemester,
                Map<String, Optional<MajorLectureOffering>> offeringCache,
                Optional<GraduationCategory> targetCatOpt,
                Map<String, GraduationCategory> categoryByLectureId
        ) {
            this.recommendedAcross = recommendedAcross;
            this.remain = remain;
            this.curGrade = curGrade;
            this.curSemester = curSemester;
            this.offeringCache = offeringCache;
            this.targetCatOpt = targetCatOpt;
            this.categoryByLectureId = categoryByLectureId;
        }
    }

    private static final class PlanningArgs {
        final int remainingSemesters;
        final RemainingSemesterCalculator.NextSemester start;
        final List<Integer> creditPlan;
        final int chapelLeftInit;
        final List<Lecture> availableLectures;
        final Map<GraduationCategory, List<String>> haveToByCategory;
        final Map<String, GraduationCategory> categoryByLectureId;
        final Map<GraduationCategory, Integer> deficits;

        PlanningArgs(int remainingSemesters,
                     RemainingSemesterCalculator.NextSemester start,
                     List<Integer> creditPlan,
                     int chapelLeftInit,
                     List<Lecture> availableLectures,
                     Map<GraduationCategory, List<String>> haveToByCategory,
                     Map<String, GraduationCategory> categoryByLectureId,
                     Map<GraduationCategory, Integer> deficits) {
            this.remainingSemesters = remainingSemesters;
            this.start = start;
            this.creditPlan = creditPlan;
            this.chapelLeftInit = chapelLeftInit;
            this.availableLectures = availableLectures;
            this.haveToByCategory = haveToByCategory;
            this.categoryByLectureId = categoryByLectureId;
            this.deficits = deficits;
        }
    }

    /** haveToByCategory에서 채플을 제외한 미이수 과목 ID를 LinkedHashSet으로 수집 */
    private LinkedHashSet<String> toHaveToSet(Map<GraduationCategory, List<String>> haveToByCategory) {
        return haveToByCategory.values().stream()
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
            Map<String, GraduationCategory> categoryByLectureId,
            List<RecommendAfterTimetableResponse.LectureItem> picks
    ) {
        Lecture chapel = findFirst(availableLectures, this::isChapel);
        if (chapel == null) return 0;
        String cat = resolveCategory(chapel.getId(), categoryByLectureId);
        picks.add(toDto(chapel, cat));
        return safeCredit(chapel);
    }

    /** 다음에 고를 과목 하나 선택(없으면 null) */
    private Lecture chooseNextLecture(
            List<Lecture> mustLectures,
            List<Lecture> availableLectures,
            PickContext ctx
    ) {
        Predicate<Lecture> base = l -> !ctx.recommendedAcross.contains(l.getId())
                && safeCredit(l) > 0
                && safeCredit(l) <= ctx.remain
                && isOfferedNow(l, ctx.curGrade, ctx.curSemester, ctx.offeringCache);

        // 1) 타겟 카테고리 우선(필수 → 일반)
        if (ctx.targetCatOpt.isPresent()) {
            GraduationCategory targetCat = ctx.targetCatOpt.get();
            Predicate<Lecture> inCat = l -> lectureInCategory(l, targetCat, ctx.categoryByLectureId);

            Lecture next = pickAndRemoveFirst(mustLectures, base.and(inCat));
            if (next != null) return next;

            next = pickAndRemoveFirst(availableLectures, base.and(inCat).and(l -> !isChapel(l)));
            if (next != null) return next;
        }

        // 2) 카테고리 무관 필수 → 일반
        Lecture next = pickAndRemoveFirst(mustLectures, base);
        if (next != null) return next;

        return pickAndRemoveFirst(availableLectures, base.and(l -> !isChapel(l)));
    }

    /** 선정된 과목의 학점/카테고리를 반영하여 집계치 갱신 */
    private void applyPick(
            Lecture picked,
            int credit,
            Map<String, GraduationCategory> categoryByLectureId,
            Map<GraduationCategory, Integer> pickedByCat,
            Map<GraduationCategory, Integer> deficits
    ) {
        GraduationCategory mappedCat = categoryByLectureId.get(picked.getId());
        if (mappedCat == null || mappedCat == GraduationCategory.CHAPEL) return;

        pickedByCat.merge(mappedCat, credit, Integer::sum);
        deficits.merge(mappedCat, -credit, Integer::sum);
        Integer left = deficits.get(mappedCat);
        if (left != null && left <= 0) {
            deficits.remove(mappedCat);
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

    /** NPE 방지용 크레딧 */
    private int safeCredit(Lecture l) {
        return l.getCredit();
    }

    /** 카테고리 문자열 해석: 있으면 Enum 이름, 채플이면 CHAPEL, 없으면 '일반교양' */
    private String resolveCategory(String lectureId, Map<String, GraduationCategory> categoryByLectureId) {
        if (lectureId == null) return "일반교양";
        GraduationCategory cat = categoryByLectureId.get(lectureId);
        if (cat != null) return cat.getName();
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

    // ===== 기존 보조 컴포넌트(그대로 사용) =====
    public interface RemainingCreditsProvider {
        OptionalInt get(User user);
    }

    // NOTE: takenCredit can include 0.5 (chapel). Use double math and ceil to avoid truncation.
    @Component
    public static class DefaultRemainingCreditsProvider implements RemainingCreditsProvider {
        @Override
        public OptionalInt get(User user) {
            // total is defined by school policy (integer credits)
            int total = user.getTotalCredit();

            // taken can be fractional (e.g., 0.5 for chapel); DO NOT cast to int
            double taken = user.getTakenCredit();

            // Remaining can be fractional; for planning, round up to next int credit
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
            Map<String, Optional<MajorLectureOffering>> cache
    ) {
        if (l == null || l.getId() == null) return true; // 방어
        // 채플은 전공 개설 정보와 무관
        if (isChapel(l)) return true;

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
}