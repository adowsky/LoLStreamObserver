package com.adowsky.lolstreamobserver.impl.factory;

import com.adowsky.lolstreamobserver.impl.repository.LoLRepository;
import com.adowsky.lolstreamobserver.impl.repository.TwitchRepository;
import com.adowsky.lolstreamobserver.api.Status;
import com.adowsky.lolstreamobserver.api.lol.LoLServer;
import com.adowsky.lolstreamobserver.api.lol.Participant;
import com.adowsky.lolstreamobserver.api.lol.Summoner;
import com.adowsky.lolstreamobserver.api.lol.SummonerModel;
import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class StatusFactory {

    private final static ExecutorService SERVER_EXECUTOR = Executors.newFixedThreadPool(10);
    private final static ExecutorService PART_EXECUTOR = Executors.newFixedThreadPool(10);

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
        List<Callable<Boolean>> workers = prepareWorkers(usersOnServer, usersDataOnServer);
        try {
            invokeWorkers(workers, SERVER_EXECUTOR);
            List<Callable<Boolean>> patricipantWorkers = prepareParticipantWorkers(usersDataOnServer, result);
            invokeWorkers(patricipantWorkers, PART_EXECUTOR);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
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

    private List<Callable<Boolean>> prepareWorkers(Map<LoLServer, List<String>> usersOnServer,
                                                   Map<LoLServer, Map<String, Summoner>> usersDataOnServer) {
        List<Callable<Boolean>> workers = new ArrayList<>();
        usersOnServer.forEach(
                (LoLServer server, List<String> user) -> workers.add(
                        () -> {
                            usersDataOnServer.put(server, riot.getSummoners(user, server));
                            return true;
                        }
                )
        );
        return workers;
    }

    private void invokeWorkers(List<Callable<Boolean>> workers, ExecutorService service) throws InterruptedException {
        List<Future<Boolean>> futures = service.invokeAll(workers);
        for (Future<Boolean> fut : futures) {
            try {
                fut.get();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<Callable<Boolean>> prepareParticipantWorkers(Map<LoLServer, Map<String, Summoner>> usersDataOnServer,
                                                              Status.Builder result) {
        List<Callable<Boolean>> patricipantWorkers = new ArrayList<>();
        usersDataOnServer.forEach((server, map) -> patricipantWorkers.add(() -> {
                    Map<Long, Participant> apiResponse = riot.getParticipants(new ArrayList<>(map.values()), server);
                    result.withAnotherSummoners(prepareSummoners(apiResponse, server));
                    return true;
                }
                )
        );
        return patricipantWorkers;
    }

    private List<Participant> prepareSummoners(Map<Long, Participant> apiResponse, LoLServer server) {
        return apiResponse.entrySet()
                .stream()
                .map((e) -> this.updateParticipant(e, server))
                .collect(Collectors.toList());
    }

    private Participant updateParticipant(Map.Entry<Long, Participant> entry, LoLServer server) {
        Participant p = entry.getValue();
        return new Participant.Builder()
                .fromPrototype(p)
                .withServer(server)
                .build();
    }

}
