package com.zuehlke.carrera.javapilot.analysis;

import com.zuehlke.carrera.javapilot.config.BaseTrackConfig;
import com.zuehlke.carrera.javapilot.eventStorage.EventStorage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

import java.util.ArrayList;
import java.util.Map;

public class TrackAnalyzer {
    private EventStorage eventStorage;
    private int roundCount = 0;
    private final ElementIdentifier firstRoundIdentifier = new ElementIdentifier(BaseTrackConfig.getLeftThreshHold(), BaseTrackConfig.getRightThreshHold());

    private ArrayList<Integer>  floatingValues;
    public TrackAnalyzer(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public TrackAnalyzer() {
        floatingValues = new ArrayList<>();
    }

    private double getFloatingAverage() {
        int sum = floatingValues.stream().reduce(0, Integer::sum);
        return ((double)sum) / floatingValues.size();
    }
    public ElementIdentifier.TrackElement analyzeMessage(com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent sensorEvent ) {
        floatingValues.add(sensorEvent.getGyroZValze());
        if(floatingValues.size() > 7) {
            floatingValues.remove(0);
        }
        return firstRoundIdentifier.identify(getFloatingAverage());
    }
    public void analyzeMessage(Object message) {
        if (message instanceof SensorEvent) {
            SensorEvent event = (SensorEvent)message;
            ElementIdentifier.TrackElement trackElement = firstRoundIdentifier.identify(event.getG()[2]);
            eventStorage.storeTrackElement(event.getTimeStamp(), trackElement);
        }
        if(message instanceof RoundTimeMessage) {
            roundCount++;
            analyzeTrack();

        }
    }

    private void analyzeTrack() {
        Map<Long, ElementIdentifier.TrackElement> trackElements = eventStorage.getTrackElements();
        ArrayList<ElementIdentifier.TrackElement> oldElements = new ArrayList<>();
        ArrayList<ElementIdentifier.TrackElement> track = new ArrayList<>();
        ElementIdentifier.TrackElement lastElement = null;
        for(Map.Entry<Long, ElementIdentifier.TrackElement> entry: trackElements.entrySet()) {
            if(lastElement == null || (entry.getValue() != lastElement && oldElements.size() > 5)) {
                track.add(entry.getValue());
                oldElements.clear();
                oldElements.add(entry.getValue());
            }
            lastElement = entry.getValue();
            if(oldElements.get(0).toString().equals(entry.getValue().toString())) {
                oldElements.add(entry.getValue());
            }
        }
        eventStorage.getRound(roundCount).setTrackElements(track);
    }


}
