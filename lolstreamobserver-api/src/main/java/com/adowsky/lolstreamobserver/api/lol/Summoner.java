package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Summoner {
    private long id;
    private String name;
    private long summonerLevel;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSummonerLevel() {
        return summonerLevel;
    }

    static public class Builder{
        private Summoner summoner;

        public Builder(){
            summoner = new Summoner();
        }

        public Builder withId(long id){
            summoner.id = id;
            return this;
        }

        public Builder withName(String name){
            summoner.name = name;
            return this;
        }

        public Builder withSummonerLevel(long level){
            summoner.summonerLevel = level;
            return this;
        }

        public Summoner build(){
            return summoner;
        }
    }

}
