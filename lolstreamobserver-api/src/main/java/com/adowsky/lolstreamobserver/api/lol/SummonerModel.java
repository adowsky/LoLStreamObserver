package com.adowsky.lolstreamobserver.api.lol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummonerModel {
    @NotNull
    private String summoner;
    @NotNull
    private LoLServer server;

    public String getSummoner() {
        return summoner;
    }

    public LoLServer getServer() {
        return server;
    }

    public void setSummoner(String name) {
        this.summoner = name.replace(" ", "").toLowerCase();
    }

    public void setServer(LoLServer server) {
        this.server = server;
    }

    @Override
    public String toString() {

        return summoner + "[" + server.getRestId() + "]";
    }

    public static class Builder {
        private SummonerModel model;

        public Builder() {
            model = new SummonerModel();
        }

        public Builder withSummoner(String summoner) {
            model.summoner = summoner;
            return this;
        }

        public Builder withServer(LoLServer server) {
            model.server = server;
            return this;
        }

        public SummonerModel build() {
            return model;
        }
    }
}
