package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class RequirementSnapshot {

    private final Map<GraduationCategory, Item> items;

    @Builder
    private RequirementSnapshot(Map<GraduationCategory, Item> items) {
        this.items = items == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new EnumMap<>(items));
    }

    public Map<GraduationCategory, Integer> targets() {
        return items.values().stream()
                .filter(item -> !item.isCompleted())
                .collect(Collectors.toMap(Item::getCategory, Item::getRemainingCredit));
    }

    @Getter
    @Builder
    public static class Item {
        private final GraduationCategory category;
        private final int totalCredit;
        private final int takenCredit;

        public boolean isCompleted() {
            return takenCredit >= totalCredit;
        }
        public int getRemainingCredit() {
            return Math.max(0, totalCredit - takenCredit);
        }
    }
}