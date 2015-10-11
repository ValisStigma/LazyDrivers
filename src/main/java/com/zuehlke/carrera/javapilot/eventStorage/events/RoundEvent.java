package com.zuehlke.carrera.javapilot.eventStorage.events;

import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;

import java.util.ArrayList;
import java.util.List;

public class RoundEvent extends Event {
    private final long roundTime;
    private ArrayList<ElementIdentifier.TrackElement> trackElements;
    public RoundEvent(RoundTimeMessage message) {
        this.roundTime = message.getRoundDuration();
    }

    public void setTrackElements(List<ElementIdentifier.TrackElement> trackElements) {
        this.trackElements = new ArrayList<>(trackElements);
    }

    public List<ElementIdentifier.TrackElement> getTrackElements() {
        return new ArrayList<>(this.trackElements);
    }
    public long getRoundTime() {
        return roundTime;
    }
}
