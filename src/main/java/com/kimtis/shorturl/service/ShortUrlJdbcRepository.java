package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.ShortUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_READ = "SELECT * FROM SHORT_URL WHERE ID = ? LIMIT 1";

    public Observable<ShortUrl> findById(long id) {
        return Observable.fromCallable(
                () -> singleResultOrNull(
                    jdbcTemplate.query(QUERY_READ, new Object[]{id},
                        (rs, rowNum) -> ShortUrl.builder()
                            .id(rs.getLong("ID"))
                            .link(rs.getString("LINK"))
                            .status(rs.getInt("STATUS"))
                            .build()
                    )
                )
            )
            .doOnNext(shortUrl -> log.debug("Database get: <{}> => {}", id, shortUrl))
            .subscribeOn(Schedulers.io());
    }

    private ShortUrl singleResultOrNull(List<ShortUrl> resultSet) {
        if (resultSet != null && resultSet.size() > 0) {
            if (resultSet.size() > 1) {
                log.warn("number of resultSet expected:<1>, but was:<{}>", resultSet.size());
            }
            return resultSet.get(0);
        }
        return null;
    }
}
