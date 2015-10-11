package com.zuehlke.carrera.javapilot.eventStorage;
import com.zuehlke.carrera.javapilot.analysis.ElementIdentifier;
import com.zuehlke.carrera.javapilot.eventStorage.events.RoundEvent;
import com.zuehlke.carrera.relayapi.messages.*;

import java.util.Map;

public class EventStorage {
    final RoundStorage roundStorage = new RoundStorage();
    final PenaltyEventStorage penaltyStorage = new PenaltyEventStorage();
    final SensorEventStorage sensorStorage = new SensorEventStorage();
    final VelocityEventStorage velocityStorage = new VelocityEventStorage();
    final TrackElementStorage trackElementStorage = new TrackElementStorage();
    public EventStorage() {

    }

    private void storeEvent(VelocityMessage message) {
        velocityStorage.storeVelocityEvent(message);
    }

    private void storeEvent(RoundTimeMessage message) {
        roundStorage.storeRound(message);
    }

    private void storeEvent(PenaltyMessage message) {
        penaltyStorage.storePenaltyEvent(message);
    }

    private void storeEvent(com.zuehlke.carrera.relayapi.messages.SensorEvent message) {
        sensorStorage.storeSensorEvent(message);
    }

    public void storeEvent(Object message) {

            if (message instanceof SensorEvent) {
                storeEvent((SensorEvent) message);

            } else if (message instanceof VelocityMessage) {
                storeEvent((VelocityMessage) message);

            } else if (message instanceof PenaltyMessage ) {
               storeEvent((PenaltyMessage) message );

            } else if ( message instanceof RoundTimeMessage ) {
                storeEvent((RoundTimeMessage) message);

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
