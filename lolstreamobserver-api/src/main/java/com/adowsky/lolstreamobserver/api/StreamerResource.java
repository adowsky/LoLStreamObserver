package com.adowsky.lolstreamobserver.api;

import com.adowsky.lolstreamobserver.api.lol.SummonerModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamerResource {

    private List<SummonerModel> lolAcc;
    @NotNull
    @Size(min = 1)
    private String twitchName;

    public List<SummonerModel> getLolAcc() {
        return lolAcc;
    }

    public String getTwitchName() {
        return twitchName;
    }


    @Override
    public String toString(){
        String listString = "";
        String listSize = "";
        if(lolAcc != null) {
            listString = lolAcc.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            listSize = String.valueOf(lolAcc.size());
        }
        return "Streamer: " + twitchName + " with accounts "+ listSize +  ": "+ listString;
    }
}
