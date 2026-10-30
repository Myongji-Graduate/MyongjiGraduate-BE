package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureRead;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FindTakenLecturePort {

    /** Empty means this adapter requires the ordinary user-then-list path, not an empty course list. */
    default Optional<TakenLectureRead> findReadOnlySnapshotByUserId(Long userId) {
        return Optional.empty();
    }

	List<TakenLecture> findTakenLecturesByUser(User user);

	Set<TakenLecture> findTakenLectureSetByUser(User user);

	List<String> findTakenLectureIdsByUserAndCodes(Long userId, Collection<String> codes);
}
