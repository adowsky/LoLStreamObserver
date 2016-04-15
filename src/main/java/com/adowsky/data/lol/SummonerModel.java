package com.adowsky.data.lol;


public class SummonerModel {
    private final String name;
    private final LoLServer server;
    
    public SummonerModel(String name, LoLServer server){
        this.name = name;
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public LoLServer getServer() {
        return server;
    }
    
}
