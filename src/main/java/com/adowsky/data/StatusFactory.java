package com.adowsky.data;

import com.adowsky.data.impl.StatusImpl;
import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Participant;
import com.adowsky.data.lol.Summoner;
import com.adowsky.data.lol.SummonerModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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


public abstract class StatusFactory {

    private final static ExecutorService MATCH_EXECUTOR = Executors.newFixedThreadPool(10);

    private final static String TWITCH_API_URL = "https://api.twitch.tv/kraken/streams/";

    public static Status createStatus(String twitchName, List<SummonerModel> summonerName) {
        StatusImpl result = new StatusImpl();
        ObjectMapper mapper = new ObjectMapper();
        Map<LoLServer, List<String>> usersOnServer = createSummonersOnServerMap(summonerName);
        try {
            JsonNode node = mapper.readValue(new URL(TWITCH_API_URL + twitchName), JsonNode.class);
            TwitchStream stream = extractStreamFromJSon(node);
            if (stream != null) {
                result.setGame(stream.getGame());
                result.setOnline(true);
            }
            
            if (stream == null || result.getGame().equals("League of Legends")) {
                result.setMatch(findMatch(usersOnServer, result));
            }

        } catch (IOException ex) {
            //TODO handle
        }
        return result;
    }

    private static Map<LoLServer, List<String>> createSummonersOnServerMap(List<SummonerModel> summoners) {
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

    private static TwitchStream extractStreamFromJSon(JsonNode node){
        JsonNode stream = node.get("stream");
        if(stream.isNull())
            return null;
        TwitchStream result = new TwitchStream();
        result.setGame(stream.get("game").asText());
        return result;
    }
    private static Participant findMatch(Map<LoLServer, List<String>> summoners, StatusImpl status) {
        Participant match = null;
        List<Callable<Participant>> workers = new ArrayList<>();
        summoners.forEach((serv, name) -> {
            workers.add(new MatchFindWorker(status, serv, name));
        });
        try {
            List<Future<Participant>> futures = MATCH_EXECUTOR.invokeAll(workers);
            for (Future<Participant> fut : futures) {
                Participant game = match;
                try {
                    game = fut.get();
                } catch (ExecutionException | InterruptedException ex) {
                    //TODO what should happen if there is no data?
                }
                if (game != null && !game.empty()) {
                    match = game;
                }
            }
        } catch (InterruptedException ex) {
            //throw new IllegalStateException("Summoner data cannot be obtained!", ex);
        }
        return match;
    }

    private static class MatchFindWorker implements Callable<Participant> {
        private final static ExecutorService SUMMONER_EXECUTOR = Executors.newFixedThreadPool(20);
        private static final String BASE_LINK = "https://eune.api.pvp.net/api/lol/";
        private static final String SUMMONER_BY_NAME = "/v1.4/summoner/by-name/";
        private static final String CURR_GAME = "/observer-mode/rest/consumer/getSpectatorGameInfo/";

        private final List<String> summoners;
        private final LoLServer server;
        private final StatusImpl status;
        private volatile Participant result;

        public MatchFindWorker(StatusImpl s, LoLServer server, List<String> summoners) {
            this.status = s;
            this.server = server;
            this.summoners = summoners;
        }

        @Override
        public Participant call() {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Summoner> data;
            try {
                data = mapper.readValue(new URL(createSummonerRequestAddress()),
                        new TypeReference<Map<String, Summoner>>() {
                });
                List<Callable<Participant>> matchFinders = new ArrayList<>();
                summoners.stream().forEach(((String summ) -> {
                    matchFinders.add(() -> {
                        Summoner tmp = data.get(summ);
                        Participant m = null;
                        if (status.getMatch().empty() && tmp != null) {
                            try {
                                List<Participant> list = mapper.readValue(new URL(createGameRequestAddress(tmp)),
                                        new TypeReference<List<Participant>>(){});
                                for(Participant p : list){
                                    if(p.getName().equals(summ))
                                        m = p;
                                }
                            } catch (IOException ex) {
                                //TODO handle exception
                            }
                        }
                        return m;
                    });
                    try {
                        List<Future<Participant>> results = SUMMONER_EXECUTOR.invokeAll(matchFinders);
                        results.stream().forEach(this::processFutureResult);
                    } catch (InterruptedException ex) {
                        throw new IllegalStateException(ex);
                    }
                }));
            } catch (IOException ex) {
                //TODO
            }
            return result;
        }

        private String createSummonerRequestAddress() {
            StringJoiner names = new StringJoiner(",");
            summoners.stream().forEach((s) -> {
                names.add(s);
            });
            StringBuilder sb = new StringBuilder();
            sb.append(BASE_LINK)
                    .append(server.name())
                    .append(SUMMONER_BY_NAME)
                    .append(names.toString())
                    .append("?api_key=")
                    .append(RiotCredentials.API_KEY.toString());
            return sb.toString();
        }

        private String createGameRequestAddress(Summoner sm) {
            StringBuilder sb = new StringBuilder();
            sb.append(BASE_LINK)
                    .append(CURR_GAME)
                    .append(server.restId())
                    .append("/")
                    .append(sm.getName())
                    .append("?api_key=")
                    .append(RiotCredentials.API_KEY.toString());
            return sb.toString();
        }

        private void processFutureResult(Future<Participant> val) {
            try {
                Participant futureResult = val.get();
                setResult(futureResult);
            } catch (InterruptedException | ExecutionException ex) {
                //TODO handle the exception
            }
        }

        private synchronized void setResult(Participant futureResult) {
            if (!futureResult.empty()) {
                if (!result.empty()) {
                    throw new IllegalStateException("Multiple matches cannot be played at the same time!");
                }
                result = futureResult;
            }
        }

    }

}
