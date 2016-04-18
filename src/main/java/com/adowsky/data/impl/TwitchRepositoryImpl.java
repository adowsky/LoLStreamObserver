package com.adowsky.data.impl;

import com.adowsky.data.TwitchRepository;
import com.adowsky.data.TwitchStream;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import java.net.URL;
import org.springframework.stereotype.Repository;

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
        TwitchStream result = new TwitchStream();
        result.setGame(stream.get("game").asText());
        return result;
    }
}
