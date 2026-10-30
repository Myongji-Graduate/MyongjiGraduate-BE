package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import java.time.Duration;

public interface TakenLectureCacheStore extends AutoCloseable {
    String get(String key);
    void put(String key, String value, Duration ttl);
    @Override
    default void close() { }
}
