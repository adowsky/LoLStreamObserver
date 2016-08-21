package com.adowsky.lolstreamobserver.impl.factory;

import com.adowsky.lolstreamobserver.impl.repository.LoLRepository;
import com.adowsky.lolstreamobserver.impl.repository.TwitchRepository;
import com.adowsky.lolstreamobserver.api.Status;
import com.adowsky.lolstreamobserver.api.lol.LoLServer;
import com.adowsky.lolstreamobserver.api.lol.Participant;
import com.adowsky.lolstreamobserver.api.lol.Summoner;
import com.adowsky.lolstreamobserver.api.lol.SummonerModel;
import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class StatusFactory {

    private final static ExecutorService SERVER_EXECUTOR = Executors.newFixedThreadPool(10);
    private final static ExecutorService PART_EXECUTOR = Executors.newFixedThreadPool(10);

    private Logger LOGGER = LoggerFactory.getLogger(StatusFactory.class);

    private TwitchRepository twitch;

    private LoLRepository riot;

    @Autowired
    public StatusFactory(TwitchRepository twitch, LoLRepository riot) {
        this.twitch = twitch;
        this.riot = riot;
    }

    public Status createStatus(String twitchName, List<SummonerModel> summonerName) {
        Status.Builder result = new Status.Builder();
        Map<LoLServer, List<String>> usersOnServer = createSummonersOnServerMap(summonerName);
        Map<LoLServer, Map<String, Summoner>> usersDataOnServer = new HashMap<>();
        TwitchStream stream = twitch.getStreamByUsername(twitchName);
        result.withStreamer(twitchName);
        if (stream != null) {
            result.withGame(stream.getGame())
                    .withOnline(true);
        }
        try {
            invokeWorkers(prepareWorkers(usersOnServer, usersDataOnServer), SERVER_EXECUTOR);
            invokeWorkers(prepareParticipantWorkers(usersDataOnServer, result), PART_EXECUTOR);
        } catch (InterruptedException ex) {
            LOGGER.error("Creating status interrupted. Some workers may not finished successfully", ex.getMessage());
        }
        return result.build();
    }

    private Map<LoLServer, List<String>> createSummonersOnServerMap(List<SummonerModel> summoners) {
        Map<LoLServer, List<String>> serverSumms = new HashMap<>();

        summoners.forEach((summ) -> {
            List<String> list = serverSumms.get(summ.getServer());
            if (list == null) {
                list = new ArrayList<>();
                serverSumms.put(summ.getServer(), list);
            }
            list.add(summ.getSummoner());
        });
        return serverSumms;
    }


    private List<Callable<Void>> prepareWorkers(Map<LoLServer, List<String>> usersOnServer,
                                                Map<LoLServer, Map<String, Summoner>> usersDataOnServer) {
        return usersOnServer.entrySet().stream()
                .map((entry) -> riot.getSummoners(entry.getValue(), entry.getKey())
                        .<Callable<Void>>map((summoners) -> () -> {
                            usersDataOnServer.put(entry.getKey(), summoners);
                            return null;
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void invokeWorkers(List<Callable<Void>> workers, ExecutorService service) throws InterruptedException {
        for (Future<Void> fut : service.invokeAll(workers)) {
            try {
                fut.get();
            } catch (ExecutionException ex) {
                LOGGER.error("Worker has been broken during execution.", ex.getMessage());
            }
        }
    }

    private List<Callable<Void>> prepareParticipantWorkers(Map<LoLServer, Map<String, Summoner>> usersDataOnServer,
                                                           Status.Builder result) {
        return usersDataOnServer.entrySet().stream()
                .<Callable<Void>>map((entry) ->
                        (() -> this.participantWorkerJob(entry, result)))
                .collect(Collectors.toList());
    }

    private Void participantWorkerJob(Map.Entry<LoLServer, Map<String, Summoner>> entry, Status.Builder result){
        Map<Long, Participant> apiResponse = riot.getParticipants(entry.getValue().values(), entry.getKey());
        result.withAnotherSummoners(prepareSummoners(apiResponse, entry.getKey()));
        return null;
    }

    private List<Participant> prepareSummoners(Map<Long, Participant> apiResponse, LoLServer server) {
        return apiResponse.entrySet().stream()
                .map((e) -> this.updateParticipant(e, server))
                .collect(Collectors.toList());
    }

    private Participant updateParticipant(Map.Entry<Long, Participant> entry, LoLServer server) {
        return new Participant.Builder()
                .fromPrototype(entry.getValue())
                .withServer(server)
                .build();
    }

}
