package com.adowsky.data;

import com.adowsky.data.lol.SummonerModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StreamerRequestParametersWrapper {
    List<SummonerModel> lolAcc;
    String twitchName;

    public List<SummonerModel> getLolAcc() {
        return lolAcc;
    }

    public void setLolAcc(List<SummonerModel> lolAcc) {
        this.lolAcc = lolAcc;
    }

    public String getTwitchName() {
        return twitchName;
    }

    public void setTwitchName(String twitchName) {
        this.twitchName = twitchName;
    }
    
    @Override
    public String toString(){
        String listString = "";
        if(lolAcc != null)
                listString = lolAcc.stream().map(Object::toString).collect(Collectors.joining(", "));
        return "Streamer: " + twitchName + " with accounts"+ lolAcc.size() +  ": "+ listString;
    }
}
