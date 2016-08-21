package com.adowsky.lolstreamobserver.impl.repository.impl;

import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;
import com.adowsky.lolstreamobserver.impl.repository.TwitchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URL;

@Repository
public class TwitchRepositoryImpl implements TwitchRepository {

    private final static String TWITCH_API_URL = "https://api.twitch.tv/kraken/streams/";

    @Override
    public TwitchStream getStreamByUsername(String username) {
        ObjectMapper mapper = new ObjectMapper();
        TwitchStream stream = null;
        try {
            JsonNode node = mapper.readValue(new URL(TWITCH_API_URL + username), JsonNode.class);
            stream = extractStreamFromJSon(node);
        } catch (IOException ex) {
            ex.printStackTrace();   
        }
        return stream;
    }

    private static TwitchStream extractStreamFromJSon(JsonNode node) {
        JsonNode stream = node.get("stream");
        if (stream.isNull()) {
            return null;
        }
        return new TwitchStream.Builder()
                .withGame(stream.get("game").asText())
                .build();
    }
}
