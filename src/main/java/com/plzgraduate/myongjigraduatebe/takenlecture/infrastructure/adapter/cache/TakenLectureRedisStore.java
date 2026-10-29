package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import java.time.Duration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/** Owns a separate connection and command deadline without altering authentication/master Redis clients. */
public final class TakenLectureRedisStore implements TakenLectureCacheStore {
    private final LettuceConnectionFactory factory;
    private final StringRedisTemplate template;

    public TakenLectureRedisStore(LettuceConnectionFactory source, Duration timeout) {
        if (timeout.isNegative() || timeout.isZero() || timeout.compareTo(Duration.ofSeconds(2)) > 0) {
            throw new IllegalArgumentException("Personal cache timeout must be in (0, 2s]");
        }
        var builder = LettuceClientConfiguration.builder().commandTimeout(timeout)
            .shutdownTimeout(Duration.ZERO).clientOptions(ClientOptions.builder()
                .socketOptions(SocketOptions.builder().connectTimeout(timeout).build())
                .timeoutOptions(TimeoutOptions.enabled(timeout))
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS).build());
        if (source.getClientConfiguration().isUseSsl()) builder.useSsl();
        var configuration = builder.build();
        if (source.getClusterConfiguration() != null) {
            factory = new LettuceConnectionFactory(source.getClusterConfiguration(), configuration);
        } else if (source.getSentinelConfiguration() != null) {
            factory = new LettuceConnectionFactory(source.getSentinelConfiguration(), configuration);
        } else {
            factory = new LettuceConnectionFactory(source.getStandaloneConfiguration(), configuration);
        }
        factory.afterPropertiesSet();
        factory.start();
        template = new StringRedisTemplate(factory);
    }

    @Override
    public String get(String key) { return template.opsForValue().get(key); }
    @Override
    public void put(String key, String value, Duration ttl) { template.opsForValue().set(key, value, ttl); }
    @Override
    public void close() { factory.destroy(); }
}
