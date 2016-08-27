package com.adowsky.lolstreamobserver.impl.controller;

import com.adowsky.lolstreamobserver.api.Status;
import com.adowsky.lolstreamobserver.api.StreamerResource;
import com.adowsky.lolstreamobserver.api.lol.LoLServer;
import com.adowsky.lolstreamobserver.api.lol.Participant;
import com.adowsky.lolstreamobserver.api.lol.SummonerModel;
import com.adowsky.lolstreamobserver.impl.creator.StatusCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class StreamerDataControllerTest {
    private static final String TWITCH_NAME = "some_name";
    private static final String SUMMONER = "summoner";
    private static final Long SUMMONER_ID = 123L;

    @Mock
    private StatusCreator factory;

    @InjectMocks
    private StreamerDataController dataController;

    @Test
    public void shouldReturnTwitchNameOnly(){
        //given
        StreamerResource resource = new StreamerResource(Collections.emptyList(), TWITCH_NAME);
        Status status = new Status.Builder()
                .withOnline(false)
                .withStreamer(TWITCH_NAME)
                .build();
        when(factory.createStatus(TWITCH_NAME, Collections.emptyList()))
                .thenReturn(status);
        //when
        ResponseEntity<Status> response = dataController.getStreamerStatus(resource);
        //then
        assertThat(response.getBody()).isEqualToComparingFieldByField(status);
    }

    @Test
    public void shouldReturnTwitchNameWitchOnlineSummoner(){
        //given
        SummonerModel model = prepareSummonerModel();
        StreamerResource resource = new StreamerResource(Collections.singletonList(model), TWITCH_NAME);
        Status status = new Status.Builder()
                .withOnline(true)
                .withStreamer(TWITCH_NAME)
                .withAnotherSummoners(Collections.singletonList(prepareParticipant()))
                .build();
        when(factory.createStatus(TWITCH_NAME, Collections.singletonList(model)))
                .thenReturn(status);
        //when
        ResponseEntity<Status> response = dataController.getStreamerStatus(resource);
        //then
        assertThat(response.getBody()).isEqualToComparingFieldByField(status);
    }

    @Test
    public void shouldReturnTwitchNameWithOfflineSummoner(){
        //given
        SummonerModel summoner = prepareSummonerModel();
        StreamerResource resource = new StreamerResource(Collections.singletonList(summoner), TWITCH_NAME);
        Status status = new Status.Builder()
                .withOnline(false)
                .withStreamer(TWITCH_NAME)
                .build();
        when(factory.createStatus(TWITCH_NAME, Collections.singletonList(summoner)))
                .thenReturn(status);
        //when
        ResponseEntity<Status> response = dataController.getStreamerStatus(resource);
        //then
        assertThat(response.getBody()).isEqualToComparingFieldByField(status);
    }

    @Test
    public void shouldReturnEmptyList(){
        //when
        ResponseEntity<List<Status>> response = dataController.getStreamerStatus(Collections.emptyList());
        //then
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    public void shouldReturnListOfStreamers(){
        //given
        StreamerResource resource = new StreamerResource(Collections.emptyList(), TWITCH_NAME);
        Status status = new Status.Builder()
                .withOnline(false)
                .withStreamer(TWITCH_NAME)
                .build();
        when(factory.createStatus(TWITCH_NAME, Collections.emptyList()))
                .thenReturn(status);
        //when
        ResponseEntity<List<Status>> response = dataController.getStreamerStatus(Collections.singletonList(resource));
        //then
        assertThat(response.getBody()).containsExactly(status);
    }

    private SummonerModel prepareSummonerModel(){
        return new SummonerModel.Builder()
                .withServer(LoLServer.EUNE)
                .withSummoner(SUMMONER)
                .build();
    }

    private Participant prepareParticipant(){
        return new Participant.Builder()
                .withServer(LoLServer.EUNE)
                .withSummonerId(SUMMONER_ID)
                .withSummonerName(SUMMONER)
                .build();
    }

}
