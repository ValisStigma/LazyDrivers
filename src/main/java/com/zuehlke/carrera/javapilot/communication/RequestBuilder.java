package com.zuehlke.carrera.javapilot.communication;

import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;
import com.zuehlke.carrera.javapilot.eventStorage.events.RoundEvent;
import com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent;
import com.zuehlke.carrera.javapilot.eventStorage.events.VelocityEvent;
import org.json.JSONObject;

public class RequestBuilder {

    private RequestBuilder(){

    }

    private static JSONObject buildFinalPackage(JSONObject jsonObject) {
        JSONObject requestData = new JSONObject();
        requestData.put("data", jsonObject);
        return requestData;
    }

    public static JSONObject getTrackElementRequest(ElementIdentifier.TrackElement trackElement) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "trackElement");
        requestData.put("trackElement", trackElement.toString());
        return buildFinalPackage(requestData);
    }

    public static JSONObject getSensorEventRequest(SensorEvent sensorEvent) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "sensorEvent");
        requestData.put("datapoint", sensorEvent.getGyroZValze());
        requestData.put("timestamp", sensorEvent.getTimeStamp());
        return buildFinalPackage(requestData);
    }

    public static JSONObject getVelocityEventRequest(VelocityEvent velocityEvent) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "velocityEvent");
        requestData.put("datapoint", velocityEvent.getVelocity());
        requestData.put("timestamp", velocityEvent.getTimeStamp());
        return buildFinalPackage(requestData);
    }

    public static JSONObject getRoundEventRequest(RoundEvent roundEvent) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "roundEvent");
        requestData.put("roundTime", roundEvent.getRoundTime());
        return buildFinalPackage(requestData);
    }

}
