package com.zuehlke.carrera.javapilot.eventStorage.events;

import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;

public class PenaltyEvent extends Event{

    private final double actualSpeed;
    private final double speedLimit;
    private final int penalty_ms;
    private final String barrier;

    public PenaltyEvent(PenaltyMessage message) {
        actualSpeed = message.getActualSpeed();
        speedLimit = message.getSpeedLimit();
        penalty_ms = message.getPenalty_ms();
        barrier = message.getBarrier();
    }

    public double getActualSpeed() {
        return actualSpeed;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    public int getPenalty_ms() {
        return penalty_ms;
    }

    public String getBarrier() {
        return barrier;
    }
}
