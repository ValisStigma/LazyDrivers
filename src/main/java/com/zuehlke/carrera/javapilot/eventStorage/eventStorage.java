package com.zuehlke.carrera.javapilot.eventStorage;
import com.zuehlke.carrera.relayapi.messages.*;

public class EventStorage {
    final RoundStorage roundStorage = new RoundStorage();
    final PenaltyEventStorage penaltyStorage = new PenaltyEventStorage();
    final SensorEventStorage sensorStorage = new SensorEventStorage();
    final VelocityEventStorage velocityStorage = new VelocityEventStorage();
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
}
