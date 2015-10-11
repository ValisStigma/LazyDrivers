package com.zuehlke.carrera.javapilot.eventStorage.events;

import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;

public class RoundEvent extends Event {
    private final long roundTime;

    public RoundEvent(RoundTimeMessage message) {
        this.roundTime = message.getRoundDuration();
    }

    public long getRoundTime() {
        return roundTime;
    }
}
