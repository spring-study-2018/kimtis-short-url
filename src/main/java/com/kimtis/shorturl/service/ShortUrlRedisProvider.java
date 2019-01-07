package com.kimtis.shorturl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtis.shorturl.domain.ShortUrl;
import com.kimtis.shorturl.domain.CachedShortUrl;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public CachedShortUrl get(long id) {
        try {
            String value = redisConnection.sync().get(getKey(id));
            CachedShortUrl result = OBJECT_MAPPER.readValue(String.valueOf(value), CachedShortUrl.class);
            log.debug("Cache {}: {} => {}", (result != null) ? "hit" : "miss", getKey(id), result);
            return result;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void set(long id, ShortUrl shortUrl) {
        try {
            CachedShortUrl cachedShortUrl = new CachedShortUrl(shortUrl);
            String value = OBJECT_MAPPER.writeValueAsString(cachedShortUrl);
            redisConnection.sync().setex(getKey(id), expireTimeSeconds, value);
            log.debug("Cache update: {} => {}", getKey(id), value);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private String getKey(long id) {
        return String.format("%s.%s.%d", NAMESPACE, VERSION, id);
    }
}
