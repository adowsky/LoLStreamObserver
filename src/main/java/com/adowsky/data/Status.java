package com.adowsky.data;

import com.adowsky.data.lol.Match;

/**
 *
 * @author adowsky
 */
public interface Status {
    boolean isOnline();
    String getGame();
    Match getMatch();
}
