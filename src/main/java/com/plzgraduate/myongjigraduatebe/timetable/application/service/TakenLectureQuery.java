package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class TakenLectureQuery {
    private final FindTakenLecturePort findTakenLecturePort;

    public Set<String> findAlreadyTakenLectureCodes(User user) {
        return findTakenLecturePort.findTakenLecturesByUser(user).stream()
                .map(tl -> tl.getLecture().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> intersectWithTaken(Long userId, Collection<String> candidateCodes) {
        return new HashSet<>(findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, candidateCodes));
    }
}
