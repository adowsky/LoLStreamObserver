package com.adowsky.data.lol;

import java.util.List;

/**
 *
 * @author adowsky
 */
public class Participant {

    private String summonerName;
    private Integer championId;

    public String getName() {
        return summonerName;
    }

    public void setName(String name) {
        this.summonerName = name;
    }

    public Integer getChampionId() {
        return championId;
    }

    public void setChampionId(Integer championId) {
        this.championId = championId;
    }

    public boolean empty() {
        return summonerName == null;
    }

}
