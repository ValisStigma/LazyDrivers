package com.zuehlke.carrera.javapilot.communication;
import com.zuehlke.carrera.javapilot.analysis.TrackElement;
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

    public static JSONObject getTrackElementRequest(TrackElement trackElement) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "trackElement");
        requestData.put("timestamp", trackElement.getTimestamp());
        requestData.put("trackElement", trackElement.getTrackElement().toString());
        return buildFinalPackage(requestData);
    }

    public static JSONObject getSensorEventRequest(SensorEvent sensorEvent, long raceId) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "sensorEvent");
        requestData.put("datapoint", sensorEvent.getGyroZValze());
        requestData.put("timestamp", sensorEvent.getTimeStamp());
        requestData.put("raceId", raceId);
        return buildFinalPackage(requestData);
    }

    public static JSONObject getVelocityEventRequest(VelocityEvent velocityEvent, long raceId) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "velocityEvent");
        requestData.put("datapoint", velocityEvent.getVelocity());
        requestData.put("timestamp", velocityEvent.getTimeStamp());
        requestData.put("raceId", raceId);
        return buildFinalPackage(requestData);
    }

    public static JSONObject getRoundEventRequest(RoundEvent roundEvent) {
        JSONObject requestData = new JSONObject();
        requestData.put("type", "roundEvent");
        requestData.put("roundTime", roundEvent.getRoundTime());
        requestData.put("timestamp", roundEvent.getTimeStamp());
        return buildFinalPackage(requestData);
    }

}
