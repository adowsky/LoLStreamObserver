package com.adowsky.lolstreamobserver.impl.rest;

import com.adowsky.lolstreamobserver.api.twitch.StreamResource;
import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Repository
public class TwitchFacade {

        private static final String TWITCH_API_URL = "https://api.twitch.tv/kraken/streams/";

    private final RestTemplate restTemplate;

    @Autowired
    public TwitchFacade(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<TwitchStream> getStreamByUsername(String username) {
        try {
            StreamResource stream = restTemplate.getForObject(TWITCH_API_URL + username, StreamResource.class);
            return Optional.ofNullable(stream.getStream());
        } catch (HttpStatusCodeException ex) {
             return Optional.empty();
        }
    }
}
