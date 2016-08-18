package com.adowsky.lolstreamobserver.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StreamerRequest {

    private final List<StreamerResource> streamers;

    @JsonCreator
    public StreamerRequest(@JsonProperty("streamers") List<StreamerResource> streamers){
        this.streamers = streamers;
    }

    public List<StreamerResource> getStreamers() {
        return streamers;
    }
}
