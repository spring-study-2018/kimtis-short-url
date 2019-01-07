package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.ShortUrl;
import com.kimtis.shorturl.domain.CachedShortUrl;
import rx.Observable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlService {
    private final ShortUrlJdbcRepository shortUrlJdbcRepository;
    private final ShortUrlRedisProvider shortUrlRedisProvider;

    public static final String NUMERIC_AND_ALPHABETIC = "[0-9a-zA-Z]*";

    public Observable<ShortUrl> get(String code) {
        long id = ShortUrl.toId(code);
        return shortUrlRedisProvider.get(id)
            .filter(Objects::nonNull)
            .map(CachedShortUrl::getShortUrl)
            .switchIfEmpty(
                shortUrlJdbcRepository.findById(id)
                    .doOnNext(shortUrl -> shortUrlRedisProvider.set(id, shortUrl))
            );
    }
}
