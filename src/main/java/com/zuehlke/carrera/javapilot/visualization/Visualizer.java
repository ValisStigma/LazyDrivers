package com.zuehlke.carrera.javapilot.visualization;

import com.zuehlke.carrera.javapilot.communication.Hermes;
import com.zuehlke.carrera.javapilot.communication.RequestBuilder;
import com.zuehlke.carrera.javapilot.eventStorage.events.RoundEvent;
import com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent;
import com.zuehlke.carrera.javapilot.eventStorage.events.VelocityEvent;
import org.json.JSONObject;


public class Visualizer {

    private static final String url = "http://localhost:8070/api";
    private Hermes hermes;

    public Visualizer() {
        hermes = new Hermes(url);
    }

    public void sendSensorEvent(SensorEvent event) {
        JSONObject data = RequestBuilder.getSensorEventRequest(event);
        new Thread(hermes.sendObject(data)).start();
   }

    public void sendVelocityEvent(VelocityEvent event) {
        JSONObject data = RequestBuilder.getVelocityEventRequest(event);
        new Thread(hermes.sendObject(data)).start();
    }

    public void sendRoundEvent(RoundEvent event) {
        JSONObject data = RequestBuilder.getRoundEventRequest(event);
        new Thread(hermes.sendObject(data)).start();
    }

}
