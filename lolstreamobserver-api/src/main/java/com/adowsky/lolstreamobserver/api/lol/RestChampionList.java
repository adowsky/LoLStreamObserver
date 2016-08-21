package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestChampionList {
    private Map<String, ChampionData> data;

    public Map<String, ChampionData> getData() {
        return data;
    }


    public static class Builder{
        private RestChampionList championList;

        public Builder(){
            championList = new RestChampionList();
        }

        public Builder withData(Map<String, ChampionData> data){
            championList.data = data;
            return this;
        }

        public RestChampionList build(){
            return championList;
        }
    }
   
}
