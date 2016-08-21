package com.adowsky.lolstreamobserver.impl.validation;


public class ErrorMessage {

    private String type;
    private String description;

    public ErrorMessage(String type, String desc){
        this.type = type;
        this.description = desc;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
