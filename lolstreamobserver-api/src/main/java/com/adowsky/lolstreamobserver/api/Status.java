

package com.adowsky.lolstreamobserver.api;

import com.adowsky.lolstreamobserver.api.lol.Participant;

import java.util.ArrayList;
import java.util.List;


/**
 * @author adowsky
 */
public class Status {
    private boolean online;
    private String game;
    private String streamer;
    private final List<Participant> summoners;

    public Status() {
        online = false;
        game = "";
        summoners = new ArrayList<>();
    }

    public boolean isOnline() {
        return online;
    }

    public String getGame() {
        return game;
    }

    public String getStreamer() {
        return streamer;
    }

    public List<Participant> getSummoners() {
        return summoners;
    }

    public static class Builder {
        private Status status;

        public Builder() {
            status = new Status();
        }

        public Builder withOnline(boolean online) {
            status.online = online;
            return this;
        }

        public Builder withGame(String game) {
            status.game = game;
            return this;
        }

        public Builder withAnotherSummoners(List<Participant> summoner) {
            status.summoners.addAll(summoner);
            return this;
        }

        public Builder withStreamer(String streamer){
            status.streamer = streamer;
            return this;
        }

        public Status build() {
            return status;
        }

    }


}