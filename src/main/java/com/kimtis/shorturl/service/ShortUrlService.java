package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.entity.ShortUrl;
import com.kimtis.shorturl.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlService {
    private final ShortUrlRepository shortUrlRepository;
    public static final String NUMERIC_AND_ALPHABETIC = "[0-9a-zA-Z]*";

    public ShortUrl get(String code) {
        Optional<ShortUrl> optionalShortUrl = shortUrlRepository.findById(ShortUrl.toId(code));
        return optionalShortUrl.orElseThrow(
            () -> new ResourceNotFoundException("Page not found: /" + code)
        );
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
