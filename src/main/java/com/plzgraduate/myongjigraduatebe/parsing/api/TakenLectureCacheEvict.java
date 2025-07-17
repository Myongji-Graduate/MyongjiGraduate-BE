package com.plzgraduate.myongjigraduatebe.parsing.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class TakenLectureCacheEvict {
    @Value("${spring.profiles.active:}")
    private String profile;

    @CacheEvict(value = "takenLectures", key = "#userId")
    public void evictTakenLecturesCache(Long userId) {
        if ("test".equals(profile)) return;
        // 실제 환경에서는 캐시 삭제
    }
} 