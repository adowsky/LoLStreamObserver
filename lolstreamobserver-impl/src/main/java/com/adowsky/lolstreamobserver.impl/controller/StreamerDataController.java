package com.adowsky.lolstreamobserver.impl.controller;

import com.adowsky.lolstreamobserver.api.Status;
import com.adowsky.lolstreamobserver.api.StreamerResource;
import com.adowsky.lolstreamobserver.impl.factory.StatusFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StreamerDataController {

    private StatusFactory sf;

    @Autowired
    public StreamerDataController(StatusFactory sf) {
        this.sf = sf;
    }


    @RequestMapping(value = "/streamers", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Status> getStreamerStatus(@RequestBody @Valid List<StreamerResource> request) {
        return request.stream()
                .map((streamerResource ->
                        sf.createStatus(streamerResource.getTwitchName(), streamerResource.getLolAcc())))
                .collect(Collectors.toList());

    }

    /**
     * ExampleRequest:  curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d '{"twitchName": "name", "lolAcc":[]}' http://localhost:8080/streamer
     */
    @RequestMapping(value = "/streamer", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Status getStreamerStatus(@RequestBody @Valid StreamerResource request) {
        return sf.createStatus(request.getTwitchName(), request.getLolAcc());

    }

}
