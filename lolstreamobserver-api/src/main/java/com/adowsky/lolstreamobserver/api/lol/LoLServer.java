package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum LoLServer {

    NA("NA1"),
    EUW("EUW1"),
    EUNE("EUN1"),
    BR("BR1"),
    JP("JP1"),
    KR("KR"),
    LAS("LA1"),
    LAN("LA2"),
    OCE("OC1"),
    RU("RU"),
    TR("TR1");

    private final String code;

    LoLServer(String code){
        this.code = code;
    }

    public String getRestId(){
        return code;
    }

    @JsonCreator
    public static LoLServer fromString(String key){
        return (key == null) ? null : LoLServer.valueOf(key.toUpperCase());
    }
}
