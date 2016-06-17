

package com.adowsky.data.impl;

import com.adowsky.data.Status;
import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Participant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author adowsky
 */
public class StatusImpl  implements Status{
    private boolean online;
    private String game;
    private final List<Participant> summoners;
    
    public StatusImpl(){
        online = false;
        game = "";
        summoners = new ArrayList<>();
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
    public List<Participant> getSummoners() {
        return summoners;
    }

    public synchronized void addSummoner(Participant summoner, LoLServer server) {
        //List<Participant> list = summoners.get(server);
        /*if(list == null){
            list = new ArrayList<>();
            summoners.put(server, list);
        }*/
        summoner.setServer(server);
        summoners.add(summoner);
    }
     public synchronized void addSummoners(List<Participant> summoner, LoLServer server) {
       /* List<Participant> list = summoners.get(server);
        if(list == null){
            list = new ArrayList<>();
            summoners.put(server, list);
        }*/
        for(Participant s: summoner){
            s.setServer(server);
            summoners.add(s);
        }
    }
    
    

    
}