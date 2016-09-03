package com.adowsky.lolstreamobserver.api;

import com.adowsky.lolstreamobserver.api.lol.SummonerModel;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamerResource {

    private List<SummonerModel> lolAcc;

    @NotNull
    @Size(min = 1)
    private String twitchName;

    @JsonCreator
    public StreamerResource(@JsonProperty("lolAcc") List<SummonerModel> lolAcc,
                            @JsonProperty("twitchName") String twitchName){
        this.lolAcc = lolAcc;
        this.twitchName = twitchName;
    }

    public List<SummonerModel> getLolAcc() {
        return lolAcc;
    }

    public String getTwitchName() {
        return twitchName;
    }

}
