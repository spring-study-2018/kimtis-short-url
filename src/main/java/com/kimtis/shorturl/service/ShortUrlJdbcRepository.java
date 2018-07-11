package com.kimtis.shorturl.service;

import com.kimtis.shorturl.domain.ShortUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_CREATE = "INSERT INTO SHORT_URL (LINK, STATUS) VALUES (?, ?)";
    private static final String QUERY_READ = "SELECT * FROM SHORT_URL WHERE ID = ? limit 1";

    public ShortUrl findById(long id) {
        return singleResultOrNull(
            jdbcTemplate.query(QUERY_READ, new Object[]{id},
                new RowMapper<ShortUrl>() {
                    @Override
                    public ShortUrl mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ShortUrl.builder()
                                .id(rs.getLong("ID"))
                                .link(rs.getString("LINK"))
                                .status(rs.getInt("STATUS"))
                                .build();
                    }
                }
            )
        );
    }

    public ShortUrl save(ShortUrl shortUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int resultCode = jdbcTemplate.update(
            new PreparedStatementCreator() {
                 @Override
                 public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                     PreparedStatement ps = conn.prepareStatement(QUERY_CREATE, new String[]{"ID"});
                     ps.setString(1, shortUrl.getLink());
                     ps.setInt(2, shortUrl.getStatus());
                     return ps;
                 }
             },
            keyHolder
        );

        return (resultCode == 0) ? null
            : ShortUrl.builder()
                .id(keyHolder.getKey().longValue())
                .link(shortUrl.getLink())
                .status(shortUrl.getStatus())
                .build();
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
