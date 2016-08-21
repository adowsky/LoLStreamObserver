package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * @author adowsky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Participant {
    private String summonerName;
    private Long championId;
    private String championNameId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long summonerId;
    private LoLServer server;

    public LoLServer getServer() {
        return server;
    }

    public String getSummonerName() {
        return summonerName;
    }


    public Long getChampionId() {
        return championId;
    }


    public String getChampionNameId() {
        return championNameId;
    }

    
    public Long getSummonerId() {
        return summonerId;
    }

    public boolean hasSummonerId() {
        return summonerId == null;
    }
    
    @Override
    public String toString(){
        return "[summonerName="+summonerName+",championId="+championId+"]";
    }

    public static class Builder{
        private Participant participant;

        public Builder(){
            participant = new Participant();
        }

        public Builder withServer(LoLServer server){
            participant.server = server;
            return this;
        }

        public Builder withSummonerName(String summonerName){
            participant.summonerName = summonerName;
            return  this;
        }

        public Builder withChampionId(Long championId){
            participant.championId = championId;
            return this;
        }

        public Builder withChampionNameId(String championNameId){
            participant.championNameId = championNameId;
            return this;
        }

        public Builder withSummonerId(Long summonerId){
            participant.summonerId = summonerId;
            return this;
        }

        public Builder fromPrototype(Participant participant){
            if(participant != null) {
                this.participant.championId = participant.championId;
                this.participant.championNameId = participant.championNameId;
                this.participant.server = participant.server;
                this.participant.summonerId = participant.summonerId;
                this.participant.summonerName = participant.summonerName;
            }
            return this;
        }

        public Participant build(){
            return participant;
        }

    }
}
