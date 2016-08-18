package com.adowsky.lolstreamobserver.impl.controller;

import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Controller
public class IssueTestController {

    @RequestMapping(value = "/yolo/{id}", method = RequestMethod.GET,
            produces = {"application/pdf"})
    public ResponseEntity firstStage(@PathVariable("id") String id) {

        Optional<ResponseEntity> responseEntity = Optional.ofNullable(getThis(id));
        return responseEntity.map((response) -> {
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(Collections.singletonMap(CONTENT_DISPOSITION, response.getHeaders().get(CONTENT_DISPOSITION)));
            return ResponseEntity.ok()
                    .contentType(response.getHeaders().getContentType())
                    .headers(headers)
                    .cacheControl(CacheControl.maxAge(60, TimeUnit.MINUTES).cachePrivate())
                    .body(response.getBody());
        }).orElse(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        //return poo.getForEntity("http://localhost:8080/ty/"+id, byte[].class);
    }

    private ResponseEntity<byte[]> getThis(String id){
        RestTemplate poo = new RestTemplate();
        ByteArrayHttpMessageConverter c = new ByteArrayHttpMessageConverter();
        MappingJackson2HttpMessageConverter c2 = new MappingJackson2HttpMessageConverter();
        poo.setMessageConverters(Arrays.asList(c2, c));
        try {
            System.out.println(Calendar.getInstance().getTime());
            return poo.getForEntity("http://localhost:8080/ty/" + id, byte[].class);
        } catch (HttpStatusCodeException ex){
            return null;
        }
    }

    @RequestMapping(value = "/ty/{id}", method = RequestMethod.GET,
            produces = {"application/pdf"})
    public ResponseEntity secondStage(@PathVariable("id") String id) {
        String path = "http://www.pdf995.com/samples/pasdadf.pdf";
        if (id.equals("a")) {
            path = "http://www.pdf995.com/samples/pdf.pdf";
        }
        HttpHeaders headers = new HttpHeaders();
        headers.put(CONTENT_DISPOSITION, Collections.singletonList("inline; filename=twojastatra.pdf"));
        return Optional.ofNullable(poo(path))
                .map((bytes -> ResponseEntity.ok()
                        .headers(headers)
                        .body(bytes)))
                .orElse(new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND));
    }

    private byte[] poo(String path) {
        RestTemplate poo = new RestTemplate();
        ByteArrayHttpMessageConverter c = new ByteArrayHttpMessageConverter();
        MappingJackson2HttpMessageConverter c2 = new MappingJackson2HttpMessageConverter();
        poo.setMessageConverters(Arrays.asList(c2, c));
        try {
            return poo.getForEntity(path, byte[].class).getBody();
        } catch (HttpStatusCodeException e) {
            return null;
        }
    }
}
