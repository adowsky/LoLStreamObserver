package com.adowsky.lolstreamobserver.impl.repository;

import com.adowsky.lolstreamobserver.api.lol.LoLServer;
import com.adowsky.lolstreamobserver.api.lol.Participant;
import com.adowsky.lolstreamobserver.api.lol.Summoner;
import com.adowsky.lolstreamobserver.api.lol.SummonerModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoLRepository {
    Optional<Map<String, Summoner>> getSummoners(List<String> summs, LoLServer server);
    Map<Long, Participant> getParticipants(Collection<Summoner> summ, LoLServer server);
}
