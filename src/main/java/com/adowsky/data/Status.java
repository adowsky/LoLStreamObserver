package com.adowsky.data;

import com.adowsky.data.lol.Participant;

/**
 *
 * @author adowsky
 */
public interface Status {
    boolean isOnline();
    String getGame();
    Participant getMatch();
}
