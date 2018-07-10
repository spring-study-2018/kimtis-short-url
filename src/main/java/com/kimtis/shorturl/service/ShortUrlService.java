package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.ShortUrl;
import com.kimtis.shorturl.domain.cache.CachedShortUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlService {
    private final ShortUrlJdbcRepository shortUrlJdbcRepository;
    private final ShortUrlRedisProvider shortUrlRedisProvider;

    public static final String NUMERIC_AND_ALPHABETIC = "[0-9a-zA-Z]*";

    public ShortUrl get(String code) {
        long id = ShortUrl.toId(code);
        CachedShortUrl cachedShortUrl = shortUrlRedisProvider.get(id);

        if (cachedShortUrl == null) {
            // cache data not exist, find from DB
            ShortUrl shortUrlFromDB = shortUrlJdbcRepository.findById(id);

            // update cache with DB data
            shortUrlRedisProvider.set(id, shortUrlFromDB);

            log.info("return data from db => {}", code);
            return shortUrlFromDB;
        }
        else {
            log.info("return data from cache => {}", code);
            return cachedShortUrl.getShortUrl();
        }
    }

    public ShortUrl save(String link) {
        return shortUrlJdbcRepository.save(
            ShortUrl.builder()
                .link(link)
                .status(HttpStatus.MOVED_PERMANENTLY.value())
                .build()
        );
    }
}
