package com.kimtis.shorturl.controller.api;

import com.kimtis.shorturl.domain.entity.ShortUrl;
import com.kimtis.shorturl.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping("/api/v1/short-url")
public class ShortUrlV1Controller {
    @Autowired
    private ShortUrlService shortUrlService;

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    public ShortUrl getShortUrl_path(@PathVariable @Pattern(regexp = ShortUrlService.NUMERIC_AND_ALPHABETIC) String code) {
        return shortUrlService.get(code);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ShortUrl getShortUrl_query(@RequestParam @Pattern(regexp = ShortUrlService.NUMERIC_AND_ALPHABETIC) String code) {
        return shortUrlService.get(code);
    }
}
