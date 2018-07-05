package com.kimtis.shorturl.domain.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Builder
@Entity
public class ShortUrl {
    @Id
    @GeneratedValue
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
