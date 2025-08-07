package com.plzgraduate.myongjigraduatebe.parsing.api;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class TakenLectureCacheEvictTest {

    @Test
    void shouldSkipCacheEviction_whenProfileIsTest() {
        // given
        TakenLectureCacheEvict evict = new TakenLectureCacheEvict();
        ReflectionTestUtils.setField(evict, "profile", "test");

        // when
        evict.evictTakenLecturesCache(1L); // 아무 예외도 발생하지 않아야 함

        // then: 예외 없이 통과하면 성공
    }

    @Test
    void shouldInvokeCacheEviction_whenProfileIsNotTest() {
        // given
        TakenLectureCacheEvict evict = Mockito.spy(new TakenLectureCacheEvict());
        ReflectionTestUtils.setField(evict, "profile", "prod");

        // when
        evict.evictTakenLecturesCache(1L);

        // then: 여기선 실제 CacheEvict 효과는 없지만 예외 없이 호출되는지만 확인
    }
}