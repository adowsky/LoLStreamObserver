package com.adowsky.data;

import com.adowsky.data.impl.StatusImpl;
import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Summoner;
import com.adowsky.data.lol.SummonerModel;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusFactory {

    private final static ExecutorService SERVER_EXECUTOR = Executors.newFixedThreadPool(10);
    private final static ExecutorService PART_EXECUTOR = Executors.newFixedThreadPool(10);
    @Autowired
    private TwitchRepository twitch;
    @Autowired
    private LoLRepository riot;
    
    public StatusFactory(){
    }

    public StatusFactory(TwitchRepository twitch, LoLRepository riot){
        this.twitch = twitch;
        this.riot = riot;
    }
    
    public Status createStatus(String twitchName, List<SummonerModel> summonerName) {
        StatusImpl result = new StatusImpl();
        Map<LoLServer, List<String>> usersOnServer = createSummonersOnServerMap(summonerName);
        Map<LoLServer, Map<String, Summoner>> usersDataOnServer = new HashMap<>();
        TwitchStream stream = twitch.getStreamByUsername(twitchName);
        if (stream != null) {
            result.setGame(stream.getGame());
            result.setOnline(true);
        }
        List<Callable<Boolean>> workers = new ArrayList<>();
        usersOnServer.forEach((LoLServer server, List<String> user) -> {
            workers.add((Callable<Boolean>) () -> {
                usersDataOnServer.put(server, riot.getSummoners(user, server));
                return true;
            });
        });
        try{
            List<Future<Boolean>> futures = SERVER_EXECUTOR.invokeAll(workers);
            for(Future<Boolean> fut : futures){
                try {
                    fut.get();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                }
            }
            List<Callable<Boolean>> patricipantWorkers = new ArrayList<>();
            usersDataOnServer.forEach((server, map) ->{
                patricipantWorkers.add((Callable<Boolean>) ()-> {
                    Map<String, Summoner> apiResponse = riot.getParticipants(new ArrayList(map.values()), server);
                    result.addSummoners(new ArrayList(apiResponse.values()), server);           
                    return true;
                });
            });
            futures = PART_EXECUTOR.invokeAll(patricipantWorkers);
            for(Future<Boolean> fut : futures){
                try {
                    fut.get();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                }
            }
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
        
        return result;
    }

    private Map<LoLServer, List<String>> createSummonersOnServerMap(List<SummonerModel> summoners) {
        Map<LoLServer, List<String>> serverSumms = new HashMap<>();
        summoners.stream().forEach((summ) -> {
            List<String> list = serverSumms.get(summ.getServer());
            if (list == null) {
                list = new ArrayList<>();
                serverSumms.put(summ.getServer(), list);
            }
            list.add(summ.getName());
        });
        return serverSumms;
    }
    
}
