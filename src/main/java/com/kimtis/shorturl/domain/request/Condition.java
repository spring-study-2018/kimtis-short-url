package com.kimtis.shorturl.domain.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
public class Condition {
    @URL
    private String link;
}