package com.adowsky.data.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummonerModel {
    private String name;
    private LoLServer server;
    
    public SummonerModel(){  
    }
    
    public SummonerModel(String name, LoLServer server){
        this.name = name.replace(" ", "").toLowerCase();
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public LoLServer getServer() {
        return server;
    }

    public void setName(String name) {
        this.name = name.replace(" ", "").toLowerCase();
    }

    public void setServer(LoLServer server) {
        this.server = server;
    }
    
}
