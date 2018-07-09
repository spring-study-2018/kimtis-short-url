package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.entity.ShortUrl;
import com.kimtis.shorturl.domain.model.cache.CachedShortUrl;
import com.kimtis.shorturl.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlService {
    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlRedisProvider shortUrlRedisProvider;

    public static final String NUMERIC_AND_ALPHABETIC = "[0-9a-zA-Z]*";

    public ShortUrl get(String code) {
        long id = ShortUrl.toId(code);
        CachedShortUrl cachedShortUrl = shortUrlRedisProvider.get(id);

        Optional<ShortUrl> optionalShortUrl;
        if (cachedShortUrl == null) {
            optionalShortUrl = shortUrlRepository.findById(id);
            shortUrlRedisProvider.set(id, optionalShortUrl.orElse(null));
            log.info("return data from db => {}", code);
        } else {
            optionalShortUrl = Optional.ofNullable(cachedShortUrl.getShortUrl());
            log.info("return data from cache => {}", code);
        }

        return optionalShortUrl.orElseThrow(() -> new ResourceNotFoundException("Page not found: /" + code));
    }

    public ShortUrl save(String link) {
        return shortUrlRepository.save(
            ShortUrl.builder()
                .link(link)
                .status(HttpStatus.MOVED_PERMANENTLY.value())
                .build()
        );
    }
}
