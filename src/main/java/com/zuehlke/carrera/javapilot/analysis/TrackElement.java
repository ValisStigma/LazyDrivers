package com.zuehlke.carrera.javapilot.analysis;

/**
 * Created by rafael on 10.01.2016.
 */
public class TrackElement {
    private final long timestamp;
    private final ElementIdentifier.TrackElement trackElement;

    public TrackElement(final long timestamp, final ElementIdentifier.TrackElement trackElement) {
        this.timestamp = timestamp;
        this.trackElement = trackElement;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public ElementIdentifier.TrackElement getTrackElement() {
        return this.trackElement;
    }
}
