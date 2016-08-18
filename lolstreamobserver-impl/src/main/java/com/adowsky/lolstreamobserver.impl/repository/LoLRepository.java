package com.adowsky.lolstreamobserver.impl.repository;

import com.adowsky.lolstreamobserver.api.lol.LoLServer;
import com.adowsky.lolstreamobserver.api.lol.Participant;
import com.adowsky.lolstreamobserver.api.lol.Summoner;
import com.adowsky.lolstreamobserver.api.lol.SummonerModel;

import java.util.List;
import java.util.Map;

public interface LoLRepository {
    Summoner getSummoner(SummonerModel model);
    Summoner getSummonerByName(String name, LoLServer server);
    Map<String, Summoner> getSummoners(List<String> summs, LoLServer server);
    Participant getParticipant(Summoner summ, LoLServer server);
    Map<Long, Participant> getParticipants(List<Summoner> summ, LoLServer server);
    String getChampionNameById(long id);
}
