package com.zuehlke.carrera.javapilot.eventStorage;

import com.zuehlke.carrera.javapilot.eventStorage.events.VelocityEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;

import java.util.HashMap;
import java.util.Map;

public class VelocityEventStorage {

    Map<Long, VelocityEvent> sensorEvents = new HashMap<>();
    public VelocityEventStorage() {

    }

    public void storeVelocityEvent(VelocityMessage message) {
        sensorEvents.put(message.getTimeStamp(), new VelocityEvent(message));
    }
}
