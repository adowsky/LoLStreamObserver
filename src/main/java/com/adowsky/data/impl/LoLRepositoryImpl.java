package com.adowsky.data.impl;

import com.adowsky.data.LoLRepository;
import com.adowsky.data.lol.GameResponse;
import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Participant;
import com.adowsky.data.lol.RestChampionList;
import com.adowsky.data.lol.Summoner;
import com.adowsky.data.lol.SummonerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class LoLRepositoryImpl implements LoLRepository {

    private final static ExecutorService GAME_EXECUTOR = Executors.newFixedThreadPool(20);
    private static final String BASE_LINK = ".api.pvp.net/";
    private static final String API_LOL = "api/lol/";
    private static final String SUMMONER_BY_NAME = "/v1.4/summoner/by-name/";
    private static final String CURR_GAME = "observer-mode/rest/consumer/getSpectatorGameInfo/";
    private static final String CHAMPION_LIST_URL = "http://ddragon.leagueoflegends.com/cdn/6.12.1/data/en_US/champion.json";
    private final RestTemplate rest;
    private static Map<Long, String> champions;

    public LoLRepositoryImpl() {
        rest = new RestTemplate();
        initializeChampionsMap();
        champions.forEach((k,v) ->{System.out.println("id "+k+"  val "+ v);});
    }

    @Override
    public Summoner getSummoner(SummonerModel model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Summoner getSummonerByName(String name, LoLServer server) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Summoner> getSummoners(List<String> summs, LoLServer server) {
        ObjectMapper mapper = new ObjectMapper(); //TODO to RestTemplate
        Map<String, Summoner> data = null;
        try {
            data = mapper.readValue(new URL(createSummonerRequestAddress(summs, server)),
                    new TypeReference<Map<String, Summoner>>() {
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    private String createSummonerRequestAddress(List<String> summoners, LoLServer server) {
        StringJoiner names = new StringJoiner(",");
        summoners.stream().forEach((s) -> {
            names.add(s);
        });
        StringBuilder sb = new StringBuilder();
        sb.append("https://")
                .append(server.name())
                .append(BASE_LINK)
                .append(API_LOL)
                .append(server.name())
                .append(SUMMONER_BY_NAME)
                .append(names.toString())
                .append("?api_key=")
                .append(RiotCredentials.API_KEY.toString());
        return sb.toString();
    }

    @Override
    public Participant getParticipant(Summoner summ, LoLServer server) {
//        ObjectMapper mapper = new ObjectMapper();
        Participant result = null;
        GameResponse response = rest.getForObject(createGameRequestAddress(summ, server),
                GameResponse.class);
        for (Participant p : response.getParticipants()) {
            if (p.getSummonerId().equals(summ.getId())) {
                result = p;
            }
        }
        return result;
    }

    private String createGameRequestAddress(Summoner sm, LoLServer server) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://")
                .append(server.name())
                .append(BASE_LINK)
                .append(CURR_GAME)
                .append(server.restId())
                .append("/")
                .append(sm.getId())
                .append("?api_key=")
                .append(RiotCredentials.API_KEY.toString());
        return sb.toString();
    }

    @Override
    public Map<Long, Participant> getParticipants(List<Summoner> summ, LoLServer server) {
//        ObjectMapper mapper = new ObjectMapper();
        Map<Long, Participant> result = new HashMap<>();
        List<LoLGameFindWorker> workers = new ArrayList<>();
        summ.stream().forEach((s) -> workers.add(new LoLGameFindWorker(s, server)));
        try {
            List<Future<Participant>> futureWorkers = GAME_EXECUTOR.invokeAll(workers);
            futureWorkers.stream().forEach((future) -> {
                try {
                    Participant part = future.get();
                    part.setChampionNameId(champions.get(part.getChampionId()));
                    result.put(part.getSummonerId(), part);                  
                } catch (ExecutionException | InterruptedException ex) {
                    System.out.println("ERROR during getting Summoner Data");
                }
            });
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    @Override
    public String getChampionNameById(long id) {
        return champions.get(id);
    }
    
    private void initializeChampionsMap(){
        champions = new HashMap<>();
        RestChampionList response = rest.getForObject(CHAMPION_LIST_URL, RestChampionList.class);
        response.getData().forEach((key, value) ->{
            champions.put(Long.valueOf((String)((Map)value).get("key")), (String)((Map)value).get("id"));
        });
    }
    private static class LoLGameFindWorker implements Callable<Participant> {

        private final Summoner summ;
        private final LoLServer server;
        private final RestTemplate rest;

        public LoLGameFindWorker(Summoner summ, LoLServer server) {
            this.server = server;
            this.summ = summ;
            rest = new RestTemplate();

        }

        @Override
        public Participant call() throws Exception {
            Participant result = null;
            GameResponse response = rest.getForObject(createGameRequestAddress(summ, server), GameResponse.class);
            for (Participant p : response.getParticipants()) {
                if (p.getSummonerId().equals(summ.getId())) {
                    result = p;
                    break;
                }
            }
            return result;
        }

        private String createGameRequestAddress(Summoner sm, LoLServer server) {
            StringBuilder sb = new StringBuilder();
            sb.append("https://")
                    .append(server.name())
                    .append(BASE_LINK)
                    .append(CURR_GAME)
                    .append(server.restId())
                    .append("/")
                    .append(sm.getId())
                    .append("?api_key=")
                    .append(RiotCredentials.API_KEY.toString());
            return sb.toString();
        }

    }

    
}
