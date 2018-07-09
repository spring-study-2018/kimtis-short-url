package com.kimtis.shorturl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimtis.shorturl.domain.entity.ShortUrl;
import com.kimtis.shorturl.domain.model.cache.CachedShortUrl;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlRedisProvider {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String NAMESPACE = "short-url";
    private static final String VERSION = "v1";
    private static final long EXPIRED_SECONDS = 10;

    private final StatefulRedisConnection<String, String> redisConnection;

    public CachedShortUrl get(long id) {
        try {
            String value = redisConnection.sync().get(getKey(id));
            return OBJECT_MAPPER.readValue(String.valueOf(value), CachedShortUrl.class);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void set(long id, ShortUrl shortUrl) {
        try {
            CachedShortUrl cachedShortUrl = new CachedShortUrl(shortUrl);
            String value = OBJECT_MAPPER.writeValueAsString(cachedShortUrl);
            redisConnection.sync().setex(getKey(id), EXPIRED_SECONDS, value);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private String getKey(long id) {
        return String.format("%s.%s.%d", NAMESPACE, VERSION, id);
    }
}
