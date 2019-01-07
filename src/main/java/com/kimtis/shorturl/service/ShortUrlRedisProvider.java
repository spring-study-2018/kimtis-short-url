package com.kimtis.shorturl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtis.shorturl.domain.ShortUrl;
import com.kimtis.shorturl.domain.CachedShortUrl;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.netflix.hystrix.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlRedisProvider {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String NAMESPACE = "short-url";
    private static final String VERSION = "v1";

    @Value("${spring.redis.default.expire-time-seconds}")
    private long expireTimeSeconds;

    private final StatefulRedisConnection<String, String> redisConnection;

    public Observable<CachedShortUrl> get(long id) {
        return new ShortUrlCacheGetCommand(redisConnection, id).toObservable();
    }

    public void set(long id, ShortUrl shortUrl) {
        try {
            CachedShortUrl cachedShortUrl = new CachedShortUrl(shortUrl);
            String value = OBJECT_MAPPER.writeValueAsString(cachedShortUrl);
            if (HystrixCircuitBreaker.Factory
                    .getInstance(HystrixCommandKey.Factory.asKey(ShortUrlCacheGetCommand.class.getSimpleName()))
                    .allowRequest()) {
                redisConnection.sync().setex(getKey(id), expireTimeSeconds, value);
                log.debug("Cache update: {} => {}", getKey(id), value);
            } else {
                log.debug("Cache update canceled, Circuit breaker opened!");
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static String getKey(long id) {
        return String.format("%s.%s.%d", NAMESPACE, VERSION, id);
    }

    static class ShortUrlCacheGetCommand extends HystrixObservableCommand<CachedShortUrl> {
        private static Setter setter = Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ShortUrlCacheGetCommand.class.getSimpleName()))
            .andCommandKey(HystrixCommandKey.Factory.asKey(ShortUrlCacheGetCommand.class.getSimpleName()))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter()
                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)
                    .withExecutionTimeoutEnabled(true)
                    .withExecutionTimeoutInMilliseconds(100)
            );

        private final StatefulRedisConnection<String, String> redisConnection;
        private final long id;

        ShortUrlCacheGetCommand(StatefulRedisConnection<String, String> redisConnection, long id) {
            super(setter);
            this.redisConnection = redisConnection;
            this.id = id;
        }

        @Override
        protected Observable<CachedShortUrl> construct() {
            return redisConnection.reactive()
                .get(getKey(id))
                .defaultIfEmpty(null)
                .map(value -> {
                    try {
                        log.debug("Cache {}: {} => {}", (value != null) ? "hit" : "miss", getKey(id), value);
                        return OBJECT_MAPPER.readValue(String.valueOf(value), CachedShortUrl.class);
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
        }

        @Override
        protected Observable<CachedShortUrl> resumeWithFallback() {
            return Observable.empty();
        }
    }
}
