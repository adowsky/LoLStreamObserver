package com.adowsky.lolstreamobserver.impl.creator;

import com.adowsky.lolstreamobserver.api.Status;
import com.adowsky.lolstreamobserver.api.lol.LoLServer;
import com.adowsky.lolstreamobserver.api.lol.Participant;
import com.adowsky.lolstreamobserver.api.lol.Summoner;
import com.adowsky.lolstreamobserver.api.lol.SummonerModel;
import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;
import com.adowsky.lolstreamobserver.impl.rest.RiotFacade;
import com.adowsky.lolstreamobserver.impl.rest.TwitchFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class StatusCreatorTest {
    private static final String STREAMER = "streamer";
    private static final String GAME = "myGAME";
    private static final String SUMMONER = "summoner";
    private static final String CHAMPION = "champion";
    private static final Long SUMMONER_ID = 123L;

    @Mock
    private TwitchFacade twitchFacade;

    @Mock
    private RiotFacade riotFacade;

    @InjectMocks
    private StatusCreator statusCreator;

    @Test
    public void shouldReturnStatusWithOnlineStreamer(){
        //given
        TwitchStream stream = new TwitchStream.Builder()
                .withGame(GAME)
                .build();
        when(twitchFacade.getStreamByUsername(STREAMER))
                .thenReturn(Optional.of(stream));
        Status expected = new Status.Builder()
                .withAnotherSummoners(Collections.emptyList())
                .withGame(GAME)
                .withOnline(true)
                .withStreamer(STREAMER)
                .build();
        //when
        Status status = statusCreator.createStatus(STREAMER, Collections.emptyList());
        //then
        assertThat(status).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void shouldReturnStatusWithOnlineSummoner(){
        //given
        TwitchStream stream = new TwitchStream.Builder()
                .withGame(GAME)
                .build();
        when(twitchFacade.getStreamByUsername(STREAMER))
                .thenReturn(Optional.of(stream));
        Summoner summoner = new Summoner.Builder()
                .withName(SUMMONER)
                .withId(SUMMONER_ID)
                .build();
        when(riotFacade.getSummoners(Collections.singletonList(SUMMONER), LoLServer.EUNE))
                .thenReturn(Optional.of(Collections.singletonMap(SUMMONER, summoner)));

        SummonerModel summonerModel = new SummonerModel.Builder()
                .withSummoner(SUMMONER)
                .withServer(LoLServer.EUNE)
                .build();

        Participant participant = new Participant.Builder()
                .withSummonerId(SUMMONER_ID)
                .withSummonerName(SUMMONER)
                .withServer(LoLServer.EUNE)
                .withChampionNameId(CHAMPION)
                .build();

        when(riotFacade.getParticipants(Collections.singleton(summoner), LoLServer.EUNE))
                .thenReturn(Collections.singletonMap(SUMMONER_ID, participant));

        Status expected = new Status.Builder()
                .withAnotherSummoners(Collections.singletonList(participant))
                .withGame(GAME)
                .withOnline(true)
                .withStreamer(STREAMER)
                .build();
        //when
        Status status = statusCreator.createStatus(STREAMER, Collections.singletonList(summonerModel));
        //then
        assertThat(status).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void shouldReturnStatusWithOfflineSummoner(){
        //given
        TwitchStream stream = new TwitchStream.Builder()
                .withGame(GAME)
                .build();
        when(twitchFacade.getStreamByUsername(STREAMER))
                .thenReturn(Optional.of(stream));
        Summoner summoner = new Summoner.Builder()
                .withName(SUMMONER)
                .withId(SUMMONER_ID)
                .build();
        when(riotFacade.getSummoners(Collections.singletonList(SUMMONER), LoLServer.EUNE))
                .thenReturn(Optional.of(Collections.singletonMap(SUMMONER, summoner)));

        SummonerModel summonerModel = new SummonerModel.Builder()
                .withSummoner(SUMMONER)
                .withServer(LoLServer.EUNE)
                .build();


        when(riotFacade.getParticipants(Collections.singleton(summoner), LoLServer.EUNE))
                .thenReturn(Collections.emptyMap());

        Status expected = new Status.Builder()
                .withAnotherSummoners(Collections.emptyList())
                .withGame(GAME)
                .withOnline(true)
                .withStreamer(STREAMER)
                .build();
        //when
        Status status = statusCreator.createStatus(STREAMER, Collections.singletonList(summonerModel));
        //then
        assertThat(status).isEqualToComparingFieldByField(expected);
    }

}
