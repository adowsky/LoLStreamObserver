

package com.adowsky.data.impl;

import com.adowsky.data.Status;
import com.adowsky.data.lol.Participant;


/**
 *
 * @author adowsky
 */
public class StatusImpl  implements Status{
    private boolean online;
    private String game;
    private volatile Participant match;
    
    public StatusImpl(){
        online = false;
        game = "";
        match = null;
    }
    @Override
    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
    
    @Override
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }
    
    @Override
    public synchronized Participant getMatch() {
        return match;
    }

    public synchronized void setMatch(Participant match) {
        this.match = match;
    }

    
}