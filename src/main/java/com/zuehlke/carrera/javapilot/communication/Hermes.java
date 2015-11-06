package com.zuehlke.carrera.javapilot.communication;

import org.json.JSONObject;

import java.io.IOException;

public class Hermes {
    private final String url;

    public Hermes(final String url) {
        this.url = url;
    }

    public Runnable sendObject(JSONObject toSend) {
        return new Runnable() {
            @Override
            public void run() {
                try{
                    HTTPRequest request = new HTTPRequest(url, HTTPRequest.Method.POST);
                    request.send(toSend.toString(), HTTPRequest.parseMediaType("application/json"));
                } catch (IOException e){
                    int x = 2;
                }
            }
        };
    }
}
