package com.adowsky.lolstreamobserver.impl.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RiotCredentials {
    private static final String SECRET_KEY_PROPERTY = "riot.secret";
    private static final Logger LOGGER = LoggerFactory.getLogger(RiotCredentials.class);

    private final String key;

    @Autowired
    RiotCredentials(Environment environment){
        this.key = environment.getProperty(SECRET_KEY_PROPERTY);
        if(StringUtils.isEmpty(key)){
            LOGGER.warn("RIOT secret key not found in environment!");
        } else {
            LOGGER.info("Key: "+ key);
        }
    }

    public String getKey() {
        return key;
    }
}
