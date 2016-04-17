

package com.adowsky;

import com.adowsky.data.Status;
import com.adowsky.data.StatusFactory;
import com.adowsky.data.StreamerRequestParametersWrapper;
import com.adowsky.data.lol.SummonerModel;
import java.util.Arrays;
import java.util.List;
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
    public Status getStreamerStatus(@RequestBody StreamerRequestParametersWrapper wrapper){
       return StatusFactory.createStatus(wrapper.getTwitchName(), wrapper.getLolAcc());
    }
}
