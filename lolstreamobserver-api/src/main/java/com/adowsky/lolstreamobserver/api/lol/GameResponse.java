package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameResponse {

    private long mapId;
    private List<Participant> participants;

    public long getMapId() {
        return mapId;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public static class Builder{
        private GameResponse gameResponse;

        public Builder(){
            gameResponse = new GameResponse();
        }

        public Builder withMapId(long mapId){
            gameResponse.mapId = mapId;
            return this;
        }

        public Builder withParticipants(List<Participant> participants){
            gameResponse.participants = participants;
            return this;
        }

        public GameResponse build(){
            return gameResponse;
        }
    }

    
    
}
