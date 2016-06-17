package com.adowsky.data.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummonerModel {
    private String summoner;
    private LoLServer server;
    
    public SummonerModel(){  
    }
    
    public SummonerModel(String summoner, LoLServer server){
        this.summoner = summoner.replace(" ", "").toLowerCase();
        this.server = server;
    }

    public String getSummoner() {
        return summoner;
    }

    public LoLServer getServer() {
        return server;
    }

    public void setSummoner(String name) {
        this.summoner = name.replace(" ", "").toLowerCase();
    }

    public void setServer(LoLServer server) {
        this.server = server;
    }
    @Override
    public String toString(){

        return summoner + "[" + server.restId() + "]";
    }
}
