

package com.adowsky;

import com.adowsky.data.Status;
import com.adowsky.data.StatusFactory;
import com.adowsky.data.StreamerRequestParametersWrapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    StatusFactory sf;
    
    @RequestMapping(value = "/streamer", method = RequestMethod.POST)
    public Status getStreamerStatus(@RequestBody StreamerRequestParametersWrapper wrapper){

       return sf.createStatus(wrapper.getTwitchName(), wrapper.getLolAcc());
    }
}
