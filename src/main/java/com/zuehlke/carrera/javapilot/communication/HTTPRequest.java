package com.zuehlke.carrera.javapilot.communication;

import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HTTPRequest {
    public enum Method {
        POST,
        PUT,
        DELETE,
        PATCH,
        GET,
    }
    private OkHttpClient client;
    private Request request;
    private Response response;
    private StringBuilder address;
    private Method method;
    private Headers.Builder header;
    private Map<String, String> urlParameter;

    public HTTPRequest(String address, Method method){
        this.address = new StringBuilder(address);
        this.method = method;
        this.client = new OkHttpClient();
        this.header = new Headers.Builder();
        this.urlParameter = new TreeMap<>();
    }

    /**
     * Parse a content-type for a request. Example: To send a JSON body use "application/json" as mediaType parameter
     * @param mediaType The HTTP content-type as described in <a>http://www.ietf.org/rfc/rfc2046.txt</a>
     * @return MediaType instance containing the chosen mediaType
     */
    public static MediaType parseMediaType(String mediaType){
        return MediaType.parse(mediaType + "; charset=utf-8");
    }
    public void addRequestHeader(String key, String value){
        header.add(key, value);
    }
    public void addRequestHeader(String key){
        header.add(key);
    }
    public void addUrlParameter(String key, String value) {
        urlParameter.put(key, value);
    }
    public void addUrlParameter(String key){
        urlParameter.put(key, null);
    }
    private boolean noUrlParameter(){
        return urlParameter.isEmpty();
    }

    //TODO: implement properly
    @Deprecated
    private void parsePath(){
        address.append("?");
        urlParameter.forEach((String key, String value) -> {
            if(value == null){
                address.append(key);
            } else {
                address.append(key + "=" + value);
            }
            address.append("&");
        });
        address.deleteCharAt(address.length());
    }
    public void send() throws IOException {
        parsePath();
        switch (method){
            case GET:
                request = new Request.Builder()
                        .url(address.toString())
                        .build();
                break;

        }
        response = client.newCall(request).execute();
    }
    public void send(String message, MediaType mediaType) throws IOException {
        RequestBody json = RequestBody.create(mediaType, message);
        switch (method) {
            case POST:
                request = new Request.Builder()
                        .url(address.toString())
                        .post(json)
                        .build();
                break;
            case PUT:
                request = new Request.Builder()
                        .url(address.toString())
                        .put(json)
                        .build();
                break;
            case PATCH:
                request = new Request.Builder()
                        .url(address.toString())
                        .patch(json)
                        .build();
                break;
            case DELETE:
                request = new Request.Builder()
                        .url(address.toString())
                        .delete(json)
                        .build();
                break;
        }
        response = client.newCall(request).execute();
    }
    public String getResponse(){
        return response.body().toString();
    }
    public String getAddress() {
        return address.toString();
    }
    public void setAddress(String address){
        this.address = new StringBuilder(address);
    }
    public Method getMethod(){
        return method;
    }
    public void setMethod(Method method){
        this.method = method;
    }
    public Map<String, List<String>> getRequestHeader(){
        return header.build().toMultimap();
    }
}