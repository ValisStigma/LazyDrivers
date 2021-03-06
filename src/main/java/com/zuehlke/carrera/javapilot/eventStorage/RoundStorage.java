package com.zuehlke.carrera.javapilot.eventStorage;


import com.zuehlke.carrera.javapilot.eventStorage.events.RoundEvent;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;

import java.util.HashMap;
import java.util.Map;


public class RoundStorage {
    private Map<Integer, RoundEvent> rounds = new HashMap<>();
    public RoundStorage() {

    }
    public void storeRound( RoundEvent message) {
       rounds.put(rounds.size() + 1, message);
    }

    public int getRoundCount() {
        return rounds.size();
    }

    public long getAverageTime() {
        long totalTime = 0;

        for(Map.Entry<Integer, RoundEvent> entry: rounds.entrySet()) {
            totalTime += entry.getValue().getRoundTime();
        }
        return totalTime / getRoundCount();
    }
    public RoundEvent getRound(int index) {
        return rounds.get(index);
    }

}
