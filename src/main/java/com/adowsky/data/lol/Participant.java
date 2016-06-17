package com.adowsky.data.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 *
 * @author adowsky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {

    private String summonerName;
    private Integer championId;
    private Long summonerId;
    private LoLServer server;

    public LoLServer getServer() {
        return server;
    }

    public void setServer(LoLServer server) {
        this.server = server;
    }

    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String name) {
        this.summonerName = name;
    }

    public Integer getChampionId() {
        return championId;
    }

    public void setChampionId(Integer championId) {
        this.championId = championId;
    }

    public Long getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(Long summonerId) {
        this.summonerId = summonerId;
    }
    

    public boolean empty() {
        return summonerId == null;
    }
    
    @Override
    public String toString(){
        return "[summonerName="+summonerName+",championId="+championId+"]";
    }
}
