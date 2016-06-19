package com.adowsky.data;

import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Participant;
import com.adowsky.data.lol.Summoner;
import com.adowsky.data.lol.SummonerModel;
import java.util.List;
import java.util.Map;

/**
 *
 * @author adowsky
 */
public interface LoLRepository {
    Summoner getSummoner(SummonerModel model);
    Summoner getSummonerByName(String name, LoLServer server);
    Map<String, Summoner> getSummoners(List<String> summs, LoLServer server);
    Participant getParticipant(Summoner summ, LoLServer server);
    Map<Long, Participant> getParticipants(List<Summoner> summ, LoLServer server);
    String getChampionNameById(long id);
}
