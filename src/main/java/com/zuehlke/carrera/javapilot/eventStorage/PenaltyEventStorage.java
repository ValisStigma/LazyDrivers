package com.zuehlke.carrera.javapilot.eventStorage;

import com.zuehlke.carrera.javapilot.eventStorage.events.PenaltyEvent;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import java.util.HashSet;
import java.util.Set;


public class PenaltyEventStorage {
   Set<PenaltyEvent> sensorEvents = new HashSet<>();

    public PenaltyEventStorage() {

    }

    public void storePenaltyEvent(PenaltyMessage message) {
        sensorEvents.add(new PenaltyEvent(message));
    }
}
