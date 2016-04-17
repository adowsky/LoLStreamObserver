package com.adowsky.data;

import com.adowsky.data.lol.LoLServer;
import com.adowsky.data.lol.Participant;
import java.util.List;
import java.util.Map;

/**
 *
 * @author adowsky
 */
public interface Status {
    boolean isOnline();
    String getGame();
    Map<LoLServer, List<Participant>> getSummoners();
}
