package com.zuehlke.carrera.javapilot.eventStorage.events;

import com.zuehlke.carrera.relayapi.messages.VelocityMessage;

public class VelocityEvent extends Event{

    private final long timeStamp;
    private final double velocity;

    public VelocityEvent(VelocityMessage message) {
        timeStamp = message.getTimeStamp();
        velocity = message.getVelocity();
    }


    public long getTimeStamp() {
        return timeStamp;
    }

    public double getVelocity() {
        return velocity;
    }

}
