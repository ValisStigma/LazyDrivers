package com.zuehlke.carrera.javapilot.akka.actors.powerhandler;

import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class PowerHandler extends LazyActor {
    private ActorHandler handler;
    public PowerHandler(ActorHandler handler) {
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                getPilot().tell(new PowerAction(handler.getCurrentPower()), getPilot());
            }
        });
    }
}