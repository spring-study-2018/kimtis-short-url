package com.kimtis.shorturl.controller.web;

import com.kimtis.shorturl.domain.entity.Condition;
import com.kimtis.shorturl.service.ShortUrlService;
import com.kimtis.shorturl.domain.entity.ShortUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShortUrlController {
    private final ShortUrlService shortUrlService;

    @Value("${short-url.service.home-page}")
    private String homePage;

    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    public ResponseEntity redirect(@PathVariable @Pattern(regexp = ShortUrlService.NUMERIC_AND_ALPHABETIC) String code) {
        ShortUrl shortUrl = shortUrlService.get(code);
        HttpStatus status = HttpStatus.valueOf(shortUrl.getStatus());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, shortUrl.getLink());
        return new ResponseEntity(headers, status);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView result(@Valid Condition condition, ModelAndView modelAndView) {
        ShortUrl newShortUrl = shortUrlService.save(condition.getLink());
        String code = ShortUrl.toCode(newShortUrl.getId());
        modelAndView.addObject("code", code);
        modelAndView.addObject("url", String.format("%s/%s", homePage, code));
        modelAndView.setViewName("result");
        return modelAndView;
    }
}
