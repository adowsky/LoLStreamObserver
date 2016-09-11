package com.adowsky.lolstreamobserver.impl.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class RiotCredentialsTest {

    @Test
    public void shouldReturnKey() {
        //given
        String riotKey = "key";
        Environment environment = mock(Environment.class);
        when(environment.getProperty(anyString())).thenReturn(riotKey);

        RiotCredentials credentials = new RiotCredentials(environment);

        //when
        String key = credentials.getKey();

        //then
        assertThat(key).isEqualTo(riotKey);
    }

    @Test
    public void shouldReturnEmptyKey() {
        //given
        Environment environment = mock(Environment.class);
        when(environment.getProperty(anyString())).thenReturn(null);

        RiotCredentials credentials = new RiotCredentials(environment);

        //when
        String key = credentials.getKey();

        //then
        assertThat(key).isNullOrEmpty();
    }

}
