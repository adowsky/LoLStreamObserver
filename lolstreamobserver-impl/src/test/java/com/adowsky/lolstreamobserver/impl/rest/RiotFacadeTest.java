package com.adowsky.lolstreamobserver.impl.rest;

import com.adowsky.lolstreamobserver.api.lol.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("unchecked")
public class RiotFacadeTest {
    private static final String RIOT_KEY = "key";
    private static final String SUMMONER_NAME = "name";
    private static final Long SUMMONER_ID = 123L;
    private static final Long SUMMONER_LEVEL = 30L;
    private static final LoLServer SERVER = LoLServer.EUNE;
    private static final Long MAP_ID = 123456L;
    private static final Long CHAMPION_ID = 1235L;
    private static final String CHAMPION_NAME_ID = "champion";

    private RestTemplate restTemplate;

    private RiotFacade riotFacade;

    @Before
    public void init() {
        RiotCredentials credentials = mock(RiotCredentials.class);
        restTemplate = mock(RestTemplate.class);

        when(credentials.getKey()).thenReturn(RIOT_KEY);
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new RestChampionList.Builder()
                        .withData(Collections.emptyMap())
                        .build());

        riotFacade = new RiotFacade(restTemplate, credentials);
    }

    @Test
    public void shouldReturnSummoner() {
        //given
        List<String> summoners = Collections.singletonList(SUMMONER_NAME);
        Summoner summoner = makeSummoner();
        Map<String, Summoner> responseBody = Collections.singletonMap(SUMMONER_NAME, summoner);
        ResponseEntity<Object> restResponse = ResponseEntity.ok(responseBody);
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))
        ).thenReturn(restResponse);

        //when
        Optional<Map<String, Summoner>> result = riotFacade.getSummoners(summoners, SERVER);

        //then
        assertThat(result).containsSame(responseBody);
    }

    private Summoner makeSummoner() {
        return new Summoner.Builder()
                .withId(SUMMONER_ID)
                .withName(SUMMONER_NAME)
                .withSummonerLevel(SUMMONER_LEVEL)
                .build();
    }

    @Test
    public void shouldReturnEmptySummonerOptional() {
        //given
        List<String> summoners = Collections.singletonList(SUMMONER_NAME);
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))
        ).thenThrow(HttpClientErrorException.class);

        //when
        Optional<Map<String, Summoner>> result = riotFacade.getSummoners(summoners, SERVER);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnEmptySummonerMap() {
        //given
        List<String> summoners = Collections.singletonList(SUMMONER_NAME);
        ResponseEntity<Object> restResponse = ResponseEntity.ok(Collections.emptyMap());
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))
        ).thenReturn(restResponse);

        //when
        Optional<Map<String, Summoner>> result = riotFacade.getSummoners(summoners, SERVER);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result).contains(Collections.emptyMap());
    }

    @Test
    public void shouldReturnParticipant() {
        //given
        Participant participant = prepareParticipant();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(prepareGameResponse(participant));

        //when
        Map<Long, Participant> result =
                riotFacade.getParticipants(Collections.singletonList(makeSummoner()), SERVER);

        //then
        assertThat(result)
                .containsOnlyKeys(SUMMONER_ID)
                .containsValues(participant);
    }

    private Participant prepareParticipant() {
        return new Participant.Builder()
                .withChampionId(CHAMPION_ID)
                .withServer(SERVER)
                .withSummonerId(SUMMONER_ID)
                .withSummonerName(SUMMONER_NAME)
                .build();
    }

    private GameResponse prepareGameResponse(Participant participant) {
        return new GameResponse.Builder()
                .withMapId(MAP_ID)
                .withParticipants(Collections.singletonList(participant))
                .build();
    }

    @Test
    public void shouldReturnEmptyParticipantMap() {
        //given
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(HttpClientErrorException.class);

        //when
        Map<Long, Participant> result =
                riotFacade.getParticipants(Collections.singletonList(makeSummoner()), SERVER);

        //then
        assertThat(result).isEmpty();
    }
}