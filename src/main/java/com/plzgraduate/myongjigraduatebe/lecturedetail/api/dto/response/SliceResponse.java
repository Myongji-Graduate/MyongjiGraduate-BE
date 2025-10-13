package com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response;

import java.util.List;

import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
public class SliceResponse<T> {

    private final List<T> items;
    private final boolean hasNext;
    private final Integer nextPage;

    public SliceResponse(List<T> items, boolean hasNext, Integer nextPage) {
        this.items = items;
        this.hasNext = hasNext;
        this.nextPage = nextPage;
    }

    public static <T> SliceResponse<T> from(Slice<T> s) {
        Integer np = s.isLast() ? null : s.getNumber() + 1;
        return new SliceResponse<>(s.getContent(), !s.isLast(), np);
    }
}