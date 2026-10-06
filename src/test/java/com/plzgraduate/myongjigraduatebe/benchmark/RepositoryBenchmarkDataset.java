package com.plzgraduate.myongjigraduatebe.benchmark;

import com.plzgraduate.myongjigraduatebe.fixture.BasicAcademicalLectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.CommonCultureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.MajorFixture;
import com.plzgraduate.myongjigraduatebe.fixture.OptionalMandatoryPolicyFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

final class RepositoryBenchmarkDataset {
    static final String TRADE = "국제통상학과";
    static final String DATA = "데이터사이언스전공";
    static final String TRADE_POLICY_MAJOR = "국제통상학전공";
    static final List<String> CATEGORIES = List.of("COMMON_CULTURE", "CORE_CULTURE",
        "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR", "PRIMARY_BASIC_ACADEMICAL_CULTURE");

    record Dataset(List<Long> users, Map<String, Object> metadata) {}

    static Dataset seed(EntityManager em, PlatformTransactionManager manager) {
        return new TransactionTemplate(manager).execute(status -> {
            Map<String, Lecture> catalog = new TreeMap<>(LectureFixture.getMockLectureMap());
            if (catalog.isEmpty()) throw new IllegalStateException("Repository lecture fixture is empty");
            Map<String, LectureJpaEntity> entities = new HashMap<>();
            catalog.forEach((id, value) -> {
                var entity = LectureJpaEntity.builder().id(id).name(value.getName()).credit(value.getCredit())
                    .isRevoked(value.getIsRevoked()).duplicateCode(value.getDuplicateCode()).build();
                em.persist(entity);
                entities.put(id, entity);
            });

            Map<String, Set<MajorLecture>> majors = Map.of(TRADE, MajorFixture.국제통상_전공(),
                DATA, MajorFixture.데이터테크놀로지_전공());
            majors.values().forEach(values -> values.forEach(value -> em.persist(MajorLectureJpaEntity.builder()
                .lectureJpaEntity(entities.get(value.getLecture().getId())).major(value.getMajor())
                .mandatory(value.getIsMandatory()).startEntryYear(value.getAppliedStartEntryYear())
                .endEntryYear(value.getAppliedEndEntryYear()).build())));

            int commonRows = 0;
            for (int year : List.of(17, 20)) {
                Map<String, CommonCulture> values = new TreeMap<>();
                Set<CommonCulture> base = year == 17 ? CommonCultureFixture.공통교양_16_17()
                    : CommonCultureFixture.공통교양_20_21_22();
                base.forEach(value -> values.put(value.getLecture().getId(), value));
                CommonCultureFixture.영어레벨_Basic().forEach(value -> values.put(value.getLecture().getId(), value));
                CommonCultureFixture.영어레벨_34().forEach(value -> values.put(value.getLecture().getId(), value));
                commonRows += values.size();
                values.values().forEach(value -> em.persist(CommonCultureJpaEntity.builder()
                    .lectureJpaEntity(entities.get(value.getLecture().getId()))
                    .commonCultureCategory(value.getCommonCultureCategory()).startEntryYear(year)
                    .endEntryYear(year).build()));
            }
            var core = CoreCultureFixture.getAllCoreCulture();
            core.forEach(value -> em.persist(CoreCultureJpaEntity.builder()
                .lectureJpaEntity(entities.get(value.getLecture().getId()))
                .coreCultureCategory(value.getCoreCultureCategory()).startEntryYear(16).endEntryYear(99).build()));
            var basic = BasicAcademicalLectureFixture.경영대_학문기초교양();
            basic.forEach(value -> em.persist(BasicAcademicalCultureLectureJpaEntity.builder()
                .lectureJpaEntity(entities.get(value.getLecture().getId())).college(value.getCollege()).build()));
            em.flush();

            var policy = OptionalMandatoryPolicyFixture.policies(TRADE_POLICY_MAJOR, 20).getFirst();
            em.createNativeQuery("""
                insert into optional_mandatory_policy
                  (name, policy_key, major, required_count, required_credit, start_entry_year, end_entry_year,
                   policy_version, policy_category, status)
                values (:name, 'benchmark-trade', :major, :count, :credit, 9, 24, 1, 'MAJOR_MANDATORY', 'ACTIVE')
                """).setParameter("name", policy.getName()).setParameter("major", policy.getMajor())
                .setParameter("count", policy.getRequiredCount()).setParameter("credit", policy.getRequiredCredit())
                .executeUpdate();
            Long policyId = ((Number) em.createNativeQuery(
                "select id from optional_mandatory_policy where policy_key = 'benchmark-trade'")
                .getSingleResult()).longValue();
            policy.getCandidateLectures().forEach(candidate -> em.createNativeQuery("""
                insert into optional_mandatory_policy_lecture (policy_id, lecture_id, equivalence_key)
                values (:policy, :lecture, :equivalence)
                """).setParameter("policy", policyId).setParameter("lecture", candidate.lecture().getId())
                .setParameter("equivalence", candidate.equivalenceKey()).executeUpdate());

            List<Long> users = new ArrayList<>();
            for (String major : List.of(TRADE, DATA)) {
                Set<String> relevant = new TreeSet<>();
                majors.get(major).forEach(value -> relevant.add(value.getLecture().getId()));
                core.forEach(value -> relevant.add(value.getLecture().getId()));
                CommonCultureFixture.공통교양_16_17().forEach(value -> relevant.add(value.getLecture().getId()));
                CommonCultureFixture.공통교양_20_21_22().forEach(value -> relevant.add(value.getLecture().getId()));
                basic.forEach(value -> relevant.add(value.getLecture().getId()));
                List<String> available = relevant.stream().filter(id -> catalog.get(id).getIsRevoked() == 0).toList();
                for (int year : List.of(17, 20)) {
                    for (EnglishLevel english : EnglishLevel.values()) {
                        for (int repeat = 0; repeat < 5; repeat++) {
                            int index = users.size();
                            List<String> taken = new ArrayList<>();
                            for (int j = 0; j < 30; j++) taken.add(available.get((index * 7 + j) % available.size()));
                            var user = UserJpaEntity.builder().authId("shared-benchmark-" + index)
                                .name("Synthetic student " + index).password("not-a-login-credential")
                                .studentNumber("60%02d%04d".formatted(year, 8000 + index)).entryYear(year)
                                .major(major).englishLevel(english).studentCategory(StudentCategory.NORMAL)
                                .transferCredit("0/0/0/0").exchangeCredit("0/0/0/0/0/0/0/0/0")
                                .completedSemesterCount(4).totalCredit(major.equals(DATA) ? 134 : 128)
                                .takenCredit(taken.stream().mapToInt(id -> catalog.get(id).getCredit()).sum()).build();
                            em.persist(user);
                            users.add(user.getId());
                            taken.forEach(id -> em.persist(TakenLectureJpaEntity.builder().user(user)
                                .lecture(entities.get(id)).year(2023).semester(Semester.FIRST).build()));
                        }
                    }
                }
            }
            em.flush();
            em.clear();
            Map<String, Object> metadata = new LinkedHashMap<>();
            metadata.put("source", "repository lecture.csv and existing test fixtures; not production snapshot");
            metadata.put("students", "synthetic NORMAL students; no production personal data");
            metadata.put("users", users.size());
            metadata.put("takenPerUser", 30);
            metadata.put("majors", List.of(TRADE, DATA));
            metadata.put("entryYears", List.of(17, 20));
            metadata.put("englishLevels", EnglishLevel.values());
            metadata.put("rows", Map.of("lecture", catalog.size(), "common", commonRows, "core", core.size(),
                "major", majors.values().stream().mapToInt(Set::size).sum(), "basic", basic.size(),
                "optionalPolicy", 1, "policyCandidates", policy.getCandidateLectures().size()));
            metadata.put("limitations", "Partial repository mappings; ICT basic mapping absent; no transfer or dual majors");
            metadata.put("sourceSha256", sourceHashes());
            return new Dataset(List.copyOf(users), metadata);
        });
    }

    private static Map<String, String> sourceHashes() {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            Map<String, String> hashes = new TreeMap<>();
            String root = "src/test/java/com/plzgraduate/myongjigraduatebe/fixture/";
            for (String file : List.of("src/test/resources/lecture.csv", root + "CommonCultureFixture.java",
                root + "CoreCultureFixture.java", root + "MajorFixture.java", root + "BasicAcademicalLectureFixture.java",
                root + "OptionalMandatoryPolicyFixture.java")) {
                hashes.put(file, HexFormat.of().formatHex(digest.digest(Files.readAllBytes(Path.of(file)))));
            }
            return hashes;
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
