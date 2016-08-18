package com.adowsky.lolstreamobserver.impl.repository.impl;

import com.adowsky.lolstreamobserver.api.lol.*;
import com.adowsky.lolstreamobserver.impl.repository.LoLRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

@Repository
public class LoLRepositoryImpl implements LoLRepository {
    private static Logger LOGGER = LoggerFactory.getLogger(LoLRepositoryImpl.class);
    private final static ExecutorService GAME_EXECUTOR = Executors.newFixedThreadPool(20);
    private static final String BASE_LINK = ".api.pvp.net/";
    private static final String API_LOL = "api/lol/";
    private static final String SUMMONER_BY_NAME = "/v1.4/summoner/by-name/";
    private static final String CURR_GAME = "observer-mode/rest/consumer/getSpectatorGameInfo/";
    private static final String CHAMPION_LIST_URL = "http://ddragon.leagueoflegends.com/cdn/6.12.1/data/en_US/champion.json";

    private final Map<Long, String> champions;
    private final RestTemplate rest;



    public LoLRepositoryImpl() {
        rest = new RestTemplate();
        champions = initializeChampionsMap();
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
        summoners.forEach(names::add);
        return "https://" +
                server.name() +
                BASE_LINK +
                API_LOL +
                server.name() +
                SUMMONER_BY_NAME +
                names.toString() +
                "?api_key=" +
                RiotCredentials.API_KEY.toString();
    }

    @Override
    public Participant getParticipant(Summoner summ, LoLServer server) {
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
        return "https://" +
                server.name() +
                BASE_LINK +
                CURR_GAME +
                server.restId() +
                "/" +
                sm.getId() +
                "?api_key=" +
                RiotCredentials.API_KEY.toString();
    }

    @Override
    public Map<Long, Participant> getParticipants(List<Summoner> summ, LoLServer server) {
//        ObjectMapper mapper = new ObjectMapper();
        Map<Long, Participant> result = new HashMap<>();
        List<LoLGameFindWorker> workers = new ArrayList<>();
        summ.forEach((s) -> workers.add(new LoLGameFindWorker(s, server)));
        try {
            List<Future<Participant>> futureWorkers = GAME_EXECUTOR.invokeAll(workers);
            futureWorkers.forEach((future) -> {
                try {
                    Optional<Participant> part = Optional.ofNullable(future.get());
                    if(part.isPresent()) {
                        Participant toResult = new Participant.Builder()
                                .fromPrototype(part.get())
                                .withChampionNameId(champions.get(part.get().getChampionId()))
                                .build();
                        result.put(part.get().getSummonerId(), toResult);
                    }
                } catch (ExecutionException | InterruptedException ex) {
                    LOGGER.warn("Error during fetching summoner's game data: " + ex.getMessage());
                }
            });
        } catch (InterruptedException ex) {
            LOGGER.error("Cannot invoke workers to fetch summoner's game data");
        }
        return result;
    }

    @Override
    public String getChampionNameById(long id) {
        return champions.get(id);
    }

    private Map<Long, String> initializeChampionsMap() {
        HashMap<Long, String> champions = new HashMap<>();
        RestChampionList response = rest.getForObject(CHAMPION_LIST_URL, RestChampionList.class);
        response.getData().forEach((key, value) ->
                champions.put(Long.valueOf((String) ((Map) value).get("key")), (String) ((Map) value).get("id")));
        return champions;
    }

    private static class LoLGameFindWorker implements Callable<Participant> {

        private final Summoner summ;
        private final LoLServer server;
        private final RestTemplate rest;

        LoLGameFindWorker(Summoner summ, LoLServer server) {
            this.server = server;
            this.summ = summ;
            rest = new RestTemplate();
        }

        @Override
        public Participant call() throws Exception {
            try {
                GameResponse response = rest.getForObject(createGameRequestAddress(summ, server), GameResponse.class);
                return getWantedParticipant(response);
            } catch (HttpStatusCodeException e) {
                return null;
            }
        }

        private String createGameRequestAddress(Summoner sm, LoLServer server) {
            return "https://" +
                    server.name() +
                    BASE_LINK +
                    CURR_GAME +
                    server.restId() +
                    "/" +
                    sm.getId() +
                    "?api_key=" +
                    RiotCredentials.API_KEY.toString();
        }

        private Participant getWantedParticipant(GameResponse response) {
            return response.getParticipants().stream()
                    .filter((p) -> summ.getId() == p.getSummonerId())
                    .findFirst()
                    .orElseGet(null);
        }

    }


}
