package com.kimtis.shorturl.domain;

import lombok.Data;

@Data
public class CachedShortUrl {
    private long pushedTime;
    private ShortUrl shortUrl;

    public CachedShortUrl(ShortUrl shortUrl) {
        this.pushedTime = System.currentTimeMillis();
        this.shortUrl = shortUrl;
    }
}
