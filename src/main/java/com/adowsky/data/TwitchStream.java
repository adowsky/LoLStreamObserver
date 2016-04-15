

package com.adowsky.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author adowsky
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchStream {

    private String game;

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }
    
}
