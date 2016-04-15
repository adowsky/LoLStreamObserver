package com.adowsky.data;

import com.adowsky.data.impl.StatusImpl;
import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Match;
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


public abstract class StatusFactory {

    private final static ExecutorService MATCH_EXECUTOR = Executors.newFixedThreadPool(10);
    private final static ExecutorService SUMMONER_EXECUTOR = Executors.newFixedThreadPool(20);
    private final static String TWITCH_API_URL = "https://api.twitch.tv/kraken/streams/";

    public static Status createStatus(String twitchName, List<SummonerModel> summonerName) {
        StatusImpl result = new StatusImpl();
        ObjectMapper mapper = new ObjectMapper();
        Map<LoLServer, List<String>> usersOnServer = createSummonersOnServerMap(summonerName);
        try {
            TwitchStream stream = mapper.readValue(new URL(TWITCH_API_URL + twitchName), TwitchStream.class);
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

    private static Match findMatch(Map<LoLServer, List<String>> summoners, StatusImpl status) {
        Match match = new Match();
        List<Callable<Match>> workers = new ArrayList<>();
        summoners.forEach((serv, name) -> {
            workers.add(new MatchFindWorker(status, serv, name));
        });
        try {
            List<Future<Match>> futures = MATCH_EXECUTOR.invokeAll(workers);
            for (Future<Match> fut : futures) {
                Match game = match;
                try {
                    game = fut.get();
                } catch (ExecutionException | InterruptedException ex) {
                    //TODO what should happen if there is no data?
                }
                if (!game.isEmpty()) {
                    match = game;
                }
            }
        } catch (InterruptedException ex) {
            throw new IllegalStateException("Summoner data cannot be obtained!", ex);
        }
        return match;
    }

    private static class MatchFindWorker implements Callable<Match> {

        private static final String BASE_LINK = "https://eune.api.pvp.net/api/lol/";
        private static final String SUMMONER_BY_NAME = "/v1.4/summoner/by-name/";
        private static final String CURR_GAME = "/observer-mode/rest/consumer/getSpectatorGameInfo/";

        private final List<String> summoners;
        private final LoLServer server;
        private final StatusImpl status;
        private volatile Match result;

        public MatchFindWorker(StatusImpl s, LoLServer server, List<String> summoners) {
            this.status = s;
            this.server = server;
            this.summoners = summoners;
        }

        @Override
        public Match call() {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Summoner> data;
            try {
                data = mapper.readValue(new URL(createSummonerRequestAddress()),
                        new TypeReference<Map<String, Summoner>>() {
                });
                List<Callable<Match>> matchFinders = new ArrayList<>();
                summoners.stream().forEach(((String summ) -> {
                    matchFinders.add(() -> {
                        Summoner tmp = data.get(summ);
                        Match m = null;
                        if (status.getMatch().isEmpty() && tmp != null) {
                            try {
                                m = mapper.readValue(new URL(createGameRequestAddress(tmp)), Match.class);
                            } catch (IOException ex) {
                                //TODO handle exception
                            }
                        }
                        return m;
                    });
                    try {
                        List<Future<Match>> results = SUMMONER_EXECUTOR.invokeAll(matchFinders);
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

        private void processFutureResult(Future<Match> val) {
            try {
                Match futureResult = val.get();
                setResult(futureResult);
            } catch (InterruptedException | ExecutionException ex) {
                //TODO handle the exception
            }
        }

        private synchronized void setResult(Match futureResult) {
            if (!futureResult.isEmpty()) {
                if (!result.isEmpty()) {
                    throw new IllegalStateException("Multiple matches cannot be played at the same time!");
                }
                result = futureResult;
            }
        }

    }

}
