/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adowsky.lolstreamobserver.impl.repository;

import com.adowsky.lolstreamobserver.api.twitch.TwitchStream;

/**
 *
 * @author adowsky
 */
public interface TwitchRepository {
    TwitchStream getStreamByUsername(String username);
}
