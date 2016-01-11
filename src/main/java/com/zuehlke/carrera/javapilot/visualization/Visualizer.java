package com.zuehlke.carrera.javapilot.visualization;

import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;
import com.zuehlke.carrera.javapilot.analysis.TrackAnalyzer;
import com.zuehlke.carrera.javapilot.analysis.TrackElement;
import com.zuehlke.carrera.javapilot.communication.Hermes;
import com.zuehlke.carrera.javapilot.communication.RequestBuilder;
import com.zuehlke.carrera.javapilot.eventStorage.events.RoundEvent;
import com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent;
import com.zuehlke.carrera.javapilot.eventStorage.events.VelocityEvent;
import org.json.JSONObject;


public class Visualizer {

    private static final String url = "http://localhost:8070/api";
    private Hermes hermes;
    private TrackAnalyzer trackAnalyzer;
    public Visualizer() {
        hermes = new Hermes(url);
        trackAnalyzer = new TrackAnalyzer();
    }

    public void sendTrackElement(TrackElement trackElement) {
        JSONObject data = RequestBuilder.getTrackElementRequest(trackElement);
        new Thread(hermes.sendObject(data)).start();
    }
    public void sendSensorEvent(SensorEvent event, long raceId) {
        JSONObject data = RequestBuilder.getSensorEventRequest(event, raceId);
        new Thread(hermes.sendObject(data)).start();
   }

    public void sendVelocityEvent(VelocityEvent event, long raceId) {
        JSONObject data = RequestBuilder.getVelocityEventRequest(event, raceId);
        new Thread(hermes.sendObject(data)).start();
    }

    public void sendRoundEvent(RoundEvent event) {
        JSONObject data = RequestBuilder.getRoundEventRequest(event);
        new Thread(hermes.sendObject(data)).start();
    }

}
