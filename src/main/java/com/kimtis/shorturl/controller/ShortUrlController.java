package com.kimtis.shorturl.controller;

import com.kimtis.shorturl.domain.ShortUrl;
import com.kimtis.shorturl.service.ShortUrlService;
import rx.Observable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping("/api/v1/short-url")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    public DeferredResult<ShortUrl> getShortUrl_path(@PathVariable @Pattern(regexp = ShortUrlService.NUMERIC_AND_ALPHABETIC) String code) {
        return toDeferredResult(shortUrlService.get(code));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public DeferredResult<ShortUrl> getShortUrl_query(@RequestParam @Pattern(regexp = ShortUrlService.NUMERIC_AND_ALPHABETIC) String code) {
        return toDeferredResult(shortUrlService.get(code));
    }

    private static <T> DeferredResult<T> toDeferredResult(Observable<T> observable) {
        DeferredResult<T> deferredResult = new DeferredResult<>();
        observable.subscribe(deferredResult::setResult, deferredResult::setErrorResult);
        return deferredResult;
    }
}
