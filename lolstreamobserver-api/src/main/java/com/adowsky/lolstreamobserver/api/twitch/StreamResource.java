package com.adowsky.lolstreamobserver.api.twitch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StreamResource {
    private TwitchStream stream;

    @JsonCreator
    public StreamResource(@JsonProperty("stream") TwitchStream stream){
        this.stream = stream;
    }

    public TwitchStream getStream() {
        return stream;
    }
}
