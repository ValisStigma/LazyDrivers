package com.zuehlke.carrera.javapilot.eventStorage.events;

import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;

import java.util.ArrayList;
import java.util.List;

public class RoundEvent extends Event {
    private final long roundTime;
    private final long timeStamp;
    private ArrayList<ElementIdentifier.TrackElement> trackElements;
    public RoundEvent(RoundTimeMessage message) {
        this.roundTime = message.getRoundDuration();
        this.timeStamp = message.getTimestamp();

    }

    public void setTrackElements(List<ElementIdentifier.TrackElement> trackElements) {
        this.trackElements = new ArrayList<>(trackElements);
    }
    public  long getTimeStamp() {
        return timeStamp;
    }
    public List<ElementIdentifier.TrackElement> getTrackElements() {
        return new ArrayList<>(this.trackElements);
    }
    public long getRoundTime() {
        return roundTime;
    }
}
