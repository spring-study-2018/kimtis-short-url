package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.entity.ShortUrl;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortUrlRepository extends CrudRepository<ShortUrl, Long> {
}
