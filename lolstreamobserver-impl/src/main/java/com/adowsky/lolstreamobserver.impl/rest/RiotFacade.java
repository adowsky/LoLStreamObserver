package com.adowsky.lolstreamobserver.impl.rest;

import com.adowsky.lolstreamobserver.api.lol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RiotFacade {
    private static Logger LOGGER = LoggerFactory.getLogger(RiotFacade.class);
    private final static ExecutorService GAME_EXECUTOR = Executors.newFixedThreadPool(20);
    private static final String BASE_LINK = ".api.pvp.net/";
    private static final String API_LOL = "api/lol/";
    private static final String SUMMONER_BY_NAME = "/v1.4/summoner/by-name/";
    private static final String CURR_GAME = "observer-mode/rest/consumer/getSpectatorGameInfo/";
    private static final String CHAMPION_LIST_URL = "http://ddragon.leagueoflegends.com/cdn/6.12.1/data/en_US/champion.json";

    private final Map<Long, String> champions;
    private final RestTemplate rest;
    private final RiotCredentials riotCredentials;


    @Autowired
    public RiotFacade(RestTemplate restTemplate, RiotCredentials riotCredentials) {
        this.rest = restTemplate;
        this.riotCredentials = riotCredentials;
        champions = initializeChampionsMap();
    }

    private Map<Long, String> initializeChampionsMap() {
        try {
            return rest.getForObject(CHAMPION_LIST_URL, RestChampionList.class)
                    .getData().entrySet().stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toMap(ChampionData::getKey, ChampionData::getId));

        } catch (HttpStatusCodeException ex) {
            LOGGER.error("Error during fetching Champion List", ex.getMessage());
            return Collections.emptyMap();
        }
    }

    
    public Optional<Map<String, Summoner>> getSummoners(List<String> summs, LoLServer server) {
        try {
            return Optional.of(rest.exchange(
                    createSummonerRequestAddress(summs, server),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Summoner>>() {
                    }).getBody());
        } catch (HttpStatusCodeException ex) {
            LOGGER.warn("No summoners found: Http status: ", ex.getStatusCode());
            return Optional.empty();
        }
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
                riotCredentials.getKey();
    }

    
    public Map<Long, Participant> getParticipants(Collection<Summoner> summ, LoLServer server) {
        List<LoLGameFindWorker> workers = summ.stream()
                .map((summoner) -> new LoLGameFindWorker(summoner, server, riotCredentials.getKey()))
                .collect(Collectors.toList());
        try {
            return GAME_EXECUTOR.invokeAll(workers).stream()
                    .map(this::processParticipantFuture)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toMap(Participant::getSummonerId, Function.identity()));
        } catch (InterruptedException ex) {
            LOGGER.error("Cannot invoke workers to fetch summoner's game data");
            return Collections.emptyMap();
        }
    }

    private Optional<Participant> processParticipantFuture(Future<Participant> future) {
        try {
            return Optional.ofNullable(future.get())
                    .map(participant -> new Participant.Builder()
                            .fromPrototype(participant)
                            .withChampionNameId(champions.get(participant.getChampionId()))
                            .build());
        } catch (ExecutionException | InterruptedException ex) {
            LOGGER.error("Error during fetching summoner's game data: " + ex.getMessage());
            return Optional.empty();
        }

    }

    private static class LoLGameFindWorker implements Callable<Participant> {

        private static final RestTemplate rest = new RestTemplate();

        private final Summoner summ;
        private final LoLServer server;
        private final String credentials;

        LoLGameFindWorker(Summoner summ, LoLServer server, String credentials) {
            this.server = server;
            this.summ = summ;
            this.credentials = credentials;
        }

        
        public Participant call() throws Exception {
            try {
                GameResponse response = rest.getForObject(createGameRequestAddress(summ, server), GameResponse.class);
                return getWantedParticipant(response);
            } catch (HttpStatusCodeException e) {
                LOGGER.warn("LOL game not found. Http status: ", e.getStatusCode());
                return null;
            }
        }

        private String createGameRequestAddress(Summoner sm, LoLServer server) {
            return "https://" +
                    server.name() +
                    BASE_LINK +
                    CURR_GAME +
                    server.getRestId() +
                    "/" +
                    sm.getId() +
                    "?api_key=" +
                    credentials;
        }

        private Participant getWantedParticipant(GameResponse response) {
            return response.getParticipants().stream()
                    .filter((p) -> summ.getId() == p.getSummonerId())
                    .findFirst()
                    .orElseGet(null);
        }

    }


}
