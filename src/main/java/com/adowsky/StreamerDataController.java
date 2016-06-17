

package com.adowsky;

import com.adowsky.data.Status;
import com.adowsky.data.StatusFactory;
import com.adowsky.data.StreamerRequestParametersWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    /**
     * @ExampleRequest  curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"twitchName": "name", "lolAcc":[]}' http://localhost:8080/streamer
     * @param wrapper
     * @return 
     */
    @RequestMapping(value = "/streamer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Status getStreamerStatus(@RequestBody StreamerRequestParametersWrapper wrapper){
       System.out.println(wrapper);     
       return sf.createStatus(wrapper.getTwitchName(), wrapper.getLolAcc());
    }
}
