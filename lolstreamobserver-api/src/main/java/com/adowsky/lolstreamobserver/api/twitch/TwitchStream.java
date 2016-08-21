

package com.adowsky.lolstreamobserver.api.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author adowsky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchStream {

    private String game = "";

    public String getGame() {
        return game;
    }


    public static class Builder{
        private TwitchStream stream;

        public Builder(){
            stream = new TwitchStream();
        }

        public Builder withGame(String game){
            stream.game = game;
            return this;
        }

        public TwitchStream build(){
            return stream;
        }
    }
    
}
