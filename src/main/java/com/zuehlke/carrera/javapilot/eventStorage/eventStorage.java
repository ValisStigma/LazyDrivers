package com.zuehlke.carrera.javapilot.eventStorage;
import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;
import com.zuehlke.carrera.javapilot.analysis.TrackAnalyzer;
import com.zuehlke.carrera.javapilot.eventStorage.events.*;
import com.zuehlke.carrera.javapilot.visualization.Visualizer;
import com.zuehlke.carrera.relayapi.messages.*;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

import java.util.Map;

public class EventStorage {
    final RoundStorage roundStorage = new RoundStorage();
    final PenaltyEventStorage penaltyStorage = new PenaltyEventStorage();
    final SensorEventStorage sensorStorage = new SensorEventStorage();
    final VelocityEventStorage velocityStorage = new VelocityEventStorage();
    final TrackElementStorage trackElementStorage = new TrackElementStorage();
    private TrackAnalyzer trackAnalyzer = new TrackAnalyzer();
    final Visualizer visualizer = new Visualizer();
    public EventStorage() {

    }

    private void storeEvent(VelocityEvent message) {
        velocityStorage.storeVelocityEvent(message);
    }

    private void storeEvent(RoundEvent message) {
        roundStorage.storeRound(message);
    }

    private void storeEvent(PenaltyMessage message) {
        penaltyStorage.storePenaltyEvent(message);
    }

    private void storeEvent(com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent message) {
        sensorStorage.storeSensorEvent(message);
    }

    public void storeEvent(Object message) {

            if (message instanceof SensorEvent) {
                com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent event = new com.zuehlke.carrera.javapilot.eventStorage.events.SensorEvent(
                        (SensorEvent) message);
                storeEvent(event);
                visualizer.sendSensorEvent(event);
                visualizer.sendTrackElement(trackAnalyzer.analyzeMessage(event));

            } else if (message instanceof VelocityMessage) {
                VelocityEvent velocityEvent = new VelocityEvent((VelocityMessage)message);
                storeEvent(velocityEvent);
                visualizer.sendVelocityEvent(velocityEvent);

            } else if (message instanceof PenaltyMessage ) {
               storeEvent((PenaltyMessage) message );

            } else if ( message instanceof RoundTimeMessage ) {
                RoundEvent roundEvent = new RoundEvent((RoundTimeMessage) message);
                storeEvent(roundEvent);
                visualizer.sendRoundEvent(roundEvent);
            }
    }

    public void storeTrackElement(final long timeStamp, ElementIdentifier.TrackElement trackElement) {
        trackElementStorage.storeTrackElement(timeStamp, trackElement);
    }

    public Map<Long, ElementIdentifier.TrackElement> getTrackElements() {
        return this.trackElementStorage.getTrackElements();
    }

    public RoundEvent getRound(int index) {
        return this.roundStorage.getRound(index);
    }
}
