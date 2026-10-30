package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.TakenLectureReadContextPort.ReadContext;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.TakenLectureReadContextPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureRead;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.TransactionDefinition;

public final class CachedTakenLectureAdapter implements FindTakenLecturePort {
    private final FindTakenLecturePort delegate;
    private final TakenLectureReadContextPort contexts;
    private final TakenLectureCache cache;

    public CachedTakenLectureAdapter(FindTakenLecturePort delegate, TakenLectureReadContextPort contexts,
        TakenLectureCache cache) {
        this.delegate = delegate;
        this.contexts = contexts;
        this.cache = cache;
    }

    @Override
    public Optional<TakenLectureRead> findReadOnlySnapshotByUserId(Long userId) {
        if (!canUseCache()) return Optional.empty();
        ReadContext context = contexts.findReadContext(userId);
        return Optional.of(new TakenLectureRead(context.user(),
            TakenLectureInventory.from(new HashSet<>(findSnapshot(context.user(), context)))));
    }

    @Override
    public List<TakenLecture> findTakenLecturesByUser(User user) {
        if (!canUseCache()) {
            cache.count("transaction_bypass");
            return delegate.findTakenLecturesByUser(user);
        }
        return findSnapshot(user, contexts.findReadContext(user.getId()));
    }

    private List<TakenLecture> findSnapshot(User user, ReadContext context) {
        var loaded = new AtomicReference<List<TakenLecture>>();
        TakenLectureSnapshot snapshot = cache.get(user.getId(), context.revision(), context.catalogRevision(), () -> {
            List<TakenLecture> result = delegate.findTakenLecturesByUser(user);
            loaded.set(result);
            return TakenLectureSnapshot.from(user.getId(), context.revision(), context.catalogRevision(), result);
        });
        return loaded.get() != null ? loaded.get() : snapshot.toDomain(user);
    }

    private boolean canUseCache() { return cache.mode() != TakenLectureCache.Mode.OFF && isSnapshotRead(); }

    private boolean isSnapshotRead() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
            || !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) return false;
        // Only an explicitly configured Spring snapshot transaction can use the cache.
        // Connection.getTransactionIsolation() itself sends a SELECT in Connector/J.
        Integer isolation = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
        return isolation != null && (isolation == TransactionDefinition.ISOLATION_REPEATABLE_READ
            || isolation == TransactionDefinition.ISOLATION_SERIALIZABLE);
    }

    @Override
    public Set<TakenLecture> findTakenLectureSetByUser(User user) { return Set.copyOf(findTakenLecturesByUser(user)); }

    @Override
    public List<String> findTakenLectureIdsByUserAndCodes(Long userId, Collection<String> codes) {
        return delegate.findTakenLectureIdsByUserAndCodes(userId, codes);
    }
}
