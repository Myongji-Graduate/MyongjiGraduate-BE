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
        User user = findUserPort.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 남은 학기/시작 학기
        int remainingSemesters = remainingSemesterCalculator.from(user);
        RemainingSemesterCalculator.NextSemester start = remainingSemesterCalculator.nextSemester(user);

        // 학기별 목표학점(없으면 정책 기본)
        OptionalInt remainingCreditsOpt = remainingCreditsProvider.get(user);
        List<Integer> creditPlan = remainingCreditsOpt.isPresent()
                ? creditTargetPolicy.plan(remainingCreditsOpt.getAsInt(), remainingSemesters)
                : null;

        // 졸업요건 스냅샷으로 채플 잔여 추론
        var snapshot = requirementSnapshotQueryPort.getSnapshot(user, remainingSemesters);
        var chapelItem = snapshot.getItems().get(GraduationCategory.CHAPEL);
        int chapelLeft = 0;
        if (chapelItem != null) {
            int totalI = chapelItem.getTotalCredit();
            int takenI = chapelItem.getTakenCredit();
            chapelLeft = Math.max(0, totalI - takenI);
        }

        // 이미 수강한 과목 코드(= Lecture.id)
        Set<String> takenCodes = takenLectureQuery.findAlreadyTakenLectureCodes(user);

        // 전체 과목 → 미이수만 풀로 (채플은 잔여가 있으면 과거 이수 여부 무시)
        int finalChapelLeft = chapelLeft;
        List<Lecture> availableLectures = findLecturePort.findAllLectures().stream()
                .filter(l -> l.getIsRevoked() == 0) // 폐지 과목 제외
                .filter(l -> {
                    // 채플은 잔여가 있으면 과거 이수 여부에 상관없이 추천 풀에 포함
                    if (isChapel(l) && finalChapelLeft > 0) return true;
                    return !takenCodes.contains(l.getId());
                })
                .sorted(Comparator.comparing(Lecture::getId)) // 결정적 순서 보장
                .collect(Collectors.toCollection(ArrayList::new)); // 제거 가능한 리스트

        // per-request cache for major lecture offering
        Map<String, Optional<MajorLectureOffering>> offeringCache = new HashMap<>();

        // === 미이수(HAVE_TO) 우선 추천을 위한 사전 수집 ===
        // 스냅샷에 포함된 카테고리만 대상으로 하여, 불필요한 계산/예외를 회피
        Set<GraduationCategory> applicableCategories = snapshot.getItems().keySet();
        Map<GraduationCategory, List<String>> haveToByCategory = applicableCategories.stream()
                .collect(Collectors.toMap(
                        cat -> cat,
                        cat -> {
                            try {
                                List<String> ids = recommendedLectureExtractor.extractRecommendedLectureIds(userId, cat);
                                return (ids == null) ? List.of() : ids;
                            } catch (IllegalArgumentException e) {
                                // UNFITTED_GRADUATION_CATEGORY 등은 해당 카테고리 스킵
                                if (e.getMessage() != null && e.getMessage().contains("UNFITTED_GRADUATION_CATEGORY")) {
                                    return List.of();
                                }
                                throw e;
                            } catch (RuntimeException e) {
                                // 계산기를 찾지 못한 케이스("No calculate detail graduation case found")는 스킵
                                String msg = e.getMessage();
                                if (msg != null && msg.contains("No calculate detail graduation case found")) {
                                    return List.of();
                                }
                                throw e;
                            }
                        }
                ));

        // 강의ID → 카테고리 역방향 맵 (미이수 기준)
        Map<String, GraduationCategory> categoryByLectureId = new HashMap<>();
        haveToByCategory.forEach((cat, ids) -> {
            if (ids != null) {
                for (String id : ids) {
                    if (id != null) categoryByLectureId.put(id, cat);
                }
            }
        });
        // 카테고리별 남은 크레딧(채플 제외) - 비율 배분용
        Map<GraduationCategory, Integer> deficits = computeDeficits(snapshot);

        // 카테고리 통합 미이수 집합 (채플은 별도로 처리하므로 제외)
        Set<String> haveToSet = haveToByCategory.values().stream()
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .filter(id -> !"KMA02101".equalsIgnoreCase(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // 현재 개설 풀에서 미이수 과목만 따로 리스트업 (제거 가능한 리스트)
        List<Lecture> mustLectures = availableLectures.stream()
                .filter(l -> haveToSet.contains(l.getId()))
                .sorted(Comparator.comparing(Lecture::getId))
                .collect(Collectors.toCollection(ArrayList::new));

        // 이전 학기에 추천한 과목을 다음 학기에서 제외하기 위한 누적 세트
        Set<String> recommendedAcross = new HashSet<>();

        List<RecommendAfterTimetableResponse.SemesterBlock> semesters = new ArrayList<>(remainingSemesters);
        int grade = start.getGrade();
        int semester = start.getSemester();

        for (int i = 0; i < remainingSemesters; i++) {
            String label = grade + "-" + semester;
            int target = (creditPlan != null) ? creditPlan.get(i) : creditTargetPolicy.targetForIndex(i, remainingSemesters);
            target = Math.min(target, MAX_PER_SEMESTER);

            List<RecommendAfterTimetableResponse.LectureItem> picks = new ArrayList<>();
            int cur = 0;

            // 1) 채플 우선(잔여 있는 동안 매 학기 1개씩)
            if (chapelLeft > 0) {
                // chapel은 동일 코드(KMA02101)를 학기마다 반복 수강 가능하므로
                // 풀에서 제거하거나 recommendedAcross에 넣지 않는다.
                Lecture chapel = findFirst(availableLectures, this::isChapel);
                if (chapel != null) {
                    String cat = resolveCategory(chapel.getId(), categoryByLectureId);
                    picks.add(toDto(chapel, cat));
                    chapelLeft--;
                    cur += safeCredit(chapel);
                }
            }

            // 2) 미이수 과목 우선 채우되, 카테고리 비율(부족분 비례)로 배분
            int cap = target; // 이미 위에서 target = Math.min(target, MAX_PER_SEMESTER)로 캡핑됨
            Map<GraduationCategory, Integer> semesterQuota = makeSemesterQuota(cap - cur, deficits);
            Map<GraduationCategory, Integer> pickedByCat = new EnumMap<>(GraduationCategory.class);

            while (cur < cap) {
                final int curGrade = grade;
                final int curSemester = semester;
                int remain = cap - cur;

                // 다음에 노려야 할 카테고리(부족분이 큰 카테고리) 선택
                Optional<GraduationCategory> targetCatOpt = chooseNextCategory(semesterQuota, pickedByCat);

                Lecture next = null;
                if (targetCatOpt.isPresent()) {
                    GraduationCategory targetCat = targetCatOpt.get();

                    // 2-1) 해당 카테고리에서 미이수 과목 우선
                    next = pickAndRemoveFirst(mustLectures,
                            l -> !recommendedAcross.contains(l.getId())
                                    && safeCredit(l) > 0
                                    && safeCredit(l) <= remain
                                    && isOfferedNow(l, curGrade, curSemester, offeringCache)
                                    && lectureInCategory(l, targetCat, categoryByLectureId));

                    // 2-2) 없으면 동일 카테고리에서 일반 과목(채플 제외)
                    if (next == null) {
                        next = pickAndRemoveFirst(availableLectures,
                                l -> !recommendedAcross.contains(l.getId())
                                        && !isChapel(l)
                                        && safeCredit(l) > 0
                                        && safeCredit(l) <= remain
                                        && isOfferedNow(l, curGrade, curSemester, offeringCache)
                                        && lectureInCategory(l, targetCat, categoryByLectureId));
                    }
                }

                // 2-3) 그래도 없으면 카테고리 무시하고 전체에서 픽(기존 폴백)
                if (next == null) {
                    next = pickAndRemoveFirst(mustLectures,
                            l -> !recommendedAcross.contains(l.getId())
                                    && safeCredit(l) > 0
                                    && safeCredit(l) <= remain
                                    && isOfferedNow(l, curGrade, curSemester, offeringCache));
                }
                if (next == null) {
                    next = pickAndRemoveFirst(availableLectures,
                            l -> !recommendedAcross.contains(l.getId())
                                    && !isChapel(l)
                                    && safeCredit(l) > 0
                                    && safeCredit(l) <= remain
                                    && isOfferedNow(l, curGrade, curSemester, offeringCache));
                }

                if (next == null) break;

                String catName = resolveCategory(next.getId(), categoryByLectureId);
                picks.add(toDto(next, catName));
                recommendedAcross.add(next.getId());
                int c = safeCredit(next);
                cur += c;

                // 카테고리 집계 및 남은 쿼터/부족분 갱신
                GraduationCategory mappedCat = categoryByLectureId.get(next.getId());
                if (mappedCat != null && mappedCat != GraduationCategory.CHAPEL) {
                    pickedByCat.merge(mappedCat, c, Integer::sum);
                    // 전역 deficits(남은 총 부족분)에서도 차감하여 다음 학기에 반영
                    deficits.merge(mappedCat, -c, Integer::sum);
                    if (deficits.get(mappedCat) != null && deficits.get(mappedCat) <= 0) {
                        deficits.remove(mappedCat);
                    }
                }

                // 남은 자리(cap-cur)가 바뀌었으므로 이번 학기 쿼터도 재계산(가벼운 비용)
                if (cur < cap) {
                    semesterQuota = makeSemesterQuota(cap - cur, deficits);
                }
            }

            semesters.add(RecommendAfterTimetableResponse.SemesterBlock.builder()
                    .label(label)
                    .creditTarget(target)
                    .lectures(picks)
                    .build());

            // 다음 라벨 진전
            if (semester == 1) semester = 2;
            else { semester = 1; grade++; }
        }

        return RecommendAfterTimetableResponse.builder()
                .semestersLeft(remainingSemesters)
                .semesters(semesters)
                .build();
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
        return "KMA02101".equalsIgnoreCase(code);
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
        if ("KMA02101".equalsIgnoreCase(lectureId)) return GraduationCategory.CHAPEL.getName();
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

        if (offeringOpt.isEmpty()) {
            // 전공 매핑이 없으면(교양 등) 학기/학년 제약 없이 허용
            return true;
        }
        MajorLectureOffering offering = offeringOpt.get();
        // 학년(0은 전학년) & 학기(BOTH/해당 학기) 체크
        return offering.matchesGrade(grade) && offering.isOfferedInSemester(semester);
    }
}