package com.plzgraduate.myongjigraduatebe.timetable.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTimetableTest {

    @Test
    void builderCreatesValidUserTimetable() {
        UserTimetable userTimetable = UserTimetable.builder()
                .id(1L)
                .userId(100L)
                .timetableId(10L)
                .year(2024)
                .semester(1)
                .build();

        assertNotNull(userTimetable);
        assertEquals(1L, userTimetable.getId());
        assertEquals(100L, userTimetable.getUserId());
        assertEquals(10L, userTimetable.getTimetableId());
        assertEquals(2024, userTimetable.getYear());
        assertEquals(1, userTimetable.getSemester());
    }

    @Test
    void getterMethodsReturnCorrectValues() {
        UserTimetable userTimetable = UserTimetable.builder()
                .id(2L)
                .userId(200L)
                .timetableId(20L)
                .year(2023)
                .semester(2)
                .build();

        assertEquals(2L, userTimetable.getId());
        assertEquals(200L, userTimetable.getUserId());
        assertEquals(20L, userTimetable.getTimetableId());
        assertEquals(2023, userTimetable.getYear());
        assertEquals(2, userTimetable.getSemester());
    }

    @Test
    void objectHasNoSetters() {
        // Lombok @Getter-only with no @Setter means there should be no public setters.
        boolean hasSetter = java.util.Arrays.stream(UserTimetable.class.getMethods())
                .anyMatch(m -> m.getName().startsWith("set"));
        assertFalse(hasSetter, "UserTimetable should have no public setters (treated as immutable for our purposes)");
    }
}
