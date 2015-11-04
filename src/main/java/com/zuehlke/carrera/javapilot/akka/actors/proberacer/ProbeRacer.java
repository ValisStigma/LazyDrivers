package com.zuehlke.carrera.javapilot.akka.actors.proberacer;


import akka.actor.Props;
import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class ProbeRacer extends LazyActor {

    private ActorHandler handler;

    private int currentPower = 70;

    public ProbeRacer(ActorHandler handler) {
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {

            }
        });
    }
}
