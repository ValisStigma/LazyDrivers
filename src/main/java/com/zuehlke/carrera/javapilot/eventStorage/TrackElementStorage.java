package com.zuehlke.carrera.javapilot.eventStorage;

import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;

import java.util.TreeMap;

/**
 * Created by rafael on 11.10.2015.
 */
public class TrackElementStorage {
    TreeMap<Long, ElementIdentifier.TrackElement> trackElements = new TreeMap<>();
    public TrackElementStorage() {

    }

    public void storeTrackElement(long timeStamp, ElementIdentifier.TrackElement trackElement) {
        trackElements.put(timeStamp, trackElement);
    }

    public TreeMap<Long, ElementIdentifier.TrackElement> getTrackElements() {
        return new TreeMap<>(this.trackElements);
    }
}
