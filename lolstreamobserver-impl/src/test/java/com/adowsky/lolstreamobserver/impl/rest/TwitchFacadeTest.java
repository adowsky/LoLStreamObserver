package com.adowsky.lolstreamobserver.impl.rest;

import com.adowsky.lolstreamobserver.api.twitch.StreamResource;
import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TwitchFacadeTest {
    private static final String GAME = "game";
    private static final String USERNAME = "name";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TwitchFacade twitchFacade;

    @Test
    public void shouldReturnTwitchStream() {
        //given
        StreamResource stream = prepareStreamResource();
        when(restTemplate.getForObject(anyString(), any())).thenReturn(stream);

        //when
        Optional<TwitchStream> result = twitchFacade.getStreamByUsername(USERNAME);

        //then
        assertThat(result).isPresent().contains(stream.getStream());
    }

    private StreamResource prepareStreamResource() {
        TwitchStream stream = new TwitchStream.Builder()
                .withGame(GAME)
                .build();
        return new StreamResource(stream);
    }

    @Test
    public void shouldReturnEmptyStream() {
        //given
        StreamResource streamResource = new StreamResource(null);
        when(restTemplate.getForObject(anyString(), any())).thenReturn(streamResource);

        //when
        Optional<TwitchStream> result = twitchFacade.getStreamByUsername(USERNAME);

        //then
        assertThat(result).isNotPresent();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnEmptyOptional() {
        //given
        when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(HttpClientErrorException.class);

        //when
        Optional<TwitchStream> result = twitchFacade.getStreamByUsername(USERNAME);

        //then
        assertThat(result).isNotPresent();
    }
}
