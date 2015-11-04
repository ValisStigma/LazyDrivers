package com.zuehlke.carrera.javapilot.akka.actors.analyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.analysis.TrackAnalyzer;
import com.zuehlke.carrera.javapilot.eventStorage.EventStorage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class AnalyseRacer extends LazyActor {

    private final TrackAnalyzer trackAnalyzer;
    private final EventStorage eventStorage;

    private ActorHandler handler;

    public AnalyseRacer(ActorHandler handler) {
        this.handler = handler;

        eventStorage = new EventStorage();
        trackAnalyzer = new TrackAnalyzer(eventStorage);

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                trackAnalyzer.analyzeMessage(message);
            }
        });

        this.registerMessage(new ActorMessage<RoundTimeMessage>(RoundTimeMessage.class) {
            @Override
            public void onRecieve(RoundTimeMessage message) {
                trackAnalyzer.analyzeMessage(message);
            }
        });

    }
}
