package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChampionData {

    private Long key;
    private String id;

    public Long getKey() {
        return key;
    }

    public String getId() {
        return id;
    }
}
