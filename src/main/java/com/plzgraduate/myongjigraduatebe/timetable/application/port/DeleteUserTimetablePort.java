package com.plzgraduate.myongjigraduatebe.timetable.application.port;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteUserTimetablePort {
    void deleteAllByUser(User user);
}
