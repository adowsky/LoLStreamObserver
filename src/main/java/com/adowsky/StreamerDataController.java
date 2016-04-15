

package com.adowsky;

import com.adowsky.data.impl.StatusImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author adowsky
 */
@RestController
public class StreamerDataController {
    
    @RequestMapping(value = "/streamer", method = RequestMethod.POST)
    public StatusImpl getStreamerStatus(@RequestBody String twitchName, @RequestBody String[] lolAcc){
        StatusImpl status = new StatusImpl();
        return status;
    }
}
