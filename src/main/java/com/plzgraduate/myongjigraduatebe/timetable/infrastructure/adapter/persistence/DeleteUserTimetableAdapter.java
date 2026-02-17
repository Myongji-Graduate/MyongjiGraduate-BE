package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.timetable.application.port.DeleteUserTimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.UserTimetableRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteUserTimetableAdapter implements DeleteUserTimetablePort {

    private final UserTimetableRepository userTimetableRepository;

    @Override
    public void deleteAllByUser(User user) {
        userTimetableRepository.deleteByUserId(user.getId());
    }
}
