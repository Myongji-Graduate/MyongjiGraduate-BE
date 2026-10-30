package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.*;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/** Frozen v2 normal-read algorithm for same-run comparison; not application wiring or fallback. */
final class PreviousMembershipCacheReader {
    record Entry(Long id, String lectureId, Integer year, Semester semester, Instant createdAt) { }
    record Snapshot(Long userId, long revision, List<Entry> entries) { }
    final AnnotationConfigApplicationContext app;
    final StringRedisTemplate redis;
    final ObjectMapper mapper;
    PreviousMembershipCacheReader(AnnotationConfigApplicationContext app, StringRedisTemplate redis, ObjectMapper mapper) {
        this.app = app; this.redis = redis; this.mapper = mapper;
    }
    TakenLectureInventory find(Long id) {
        return TakenLectureCacheConsistencyIntegrationTest.tx(app, true).execute(status -> {
            User user = app.getBean(FindUserUseCase.class).findUserById(id);
            var persistence = app.getBean(TakenLecturePersistenceAdapter.class);
            var source = app.getBean(javax.sql.DataSource.class);
            var connection = org.springframework.jdbc.datasource.DataSourceUtils.getConnection(source);
            try { connection.getTransactionIsolation(); } // v2's actual-connection guard, including its metadata SELECT
            catch (java.sql.SQLException exception) { throw new IllegalStateException(exception); }
            finally { org.springframework.jdbc.datasource.DataSourceUtils.releaseConnection(connection, source); }
            long revision = persistence.findRevision(id);
            String key = "takenLectures:v2:benchmark:" + id + ":" + revision;
            String json = redis.opsForValue().get(key);
            try {
                if (json == null) {
                    var list = persistence.findTakenLecturesByUser(user);
                    var snapshot = new Snapshot(id, revision, list.stream().map(t -> new Entry(t.getId(), t.getLecture().getId(),
                        t.getYear(), t.getSemester(), t.getCreatedAt())).sorted(Comparator.comparing(Entry::id)).toList());
                    String encoded = mapper.writeValueAsString(snapshot);
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() { redis.opsForValue().set(key, encoded, Duration.ofMinutes(10)); }
                    });
                    return TakenLectureInventory.from(new HashSet<>(list));
                }
                Snapshot snapshot = mapper.readValue(json, Snapshot.class);
                var catalog = app.getBean(FindLecturePort.class).findLecturesByIds(snapshot.entries().stream()
                    .map(Entry::lectureId).distinct().toList()).stream().collect(Collectors.toMap(Lecture::getId, Function.identity()));
                return TakenLectureInventory.from(snapshot.entries().stream().map(e -> TakenLecture.builder().id(e.id()).user(user)
                    .lecture(catalog.get(e.lectureId())).year(e.year()).semester(e.semester()).createdAt(e.createdAt()).build())
                    .collect(Collectors.toSet()));
            } catch (com.fasterxml.jackson.core.JsonProcessingException exception) { throw new IllegalStateException(exception); }
        });
    }
}
