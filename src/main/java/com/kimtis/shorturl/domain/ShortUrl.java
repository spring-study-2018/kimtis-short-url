package com.kimtis.shorturl.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUrl {
    private Long id;
    private String link;
    private Integer status;

    public static String toCode(long id) {
        return Long.toString(id, Character.MAX_RADIX);
    }

    public static long toId(String code) {
        return Long.valueOf(code, Character.MAX_RADIX);
    }
}
