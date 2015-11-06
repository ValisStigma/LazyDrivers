package com.zuehlke.carrera.javapilot.eventStorage;

import com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent;

import java.util.HashMap;
import java.util.Map;

public class SensorEventStorage {
    Map<Long, SensorEvent> sensorEvents = new HashMap<>();
    public SensorEventStorage() {

    }

    public void storeSensorEvent(SensorEvent sensorEvent) {
        sensorEvents.put(sensorEvent.getTimeStamp(), sensorEvent);
    }
}
