package com.zuehlke.carrera.javapilot.akka.actors.startracer;

import akka.actor.Props;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.RaceStartMessage;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class StartRacer extends LazyActor {

    private ActorHandler handler;

    public static Props props(ActorHandler handler) {
        return Props.create(
                StartRacer.class, () -> new StartRacer(handler));
    }

    public StartRacer(ActorHandler handler){
        this.handler = handler;

        this.registerMessage(new ActorMessage<RaceStartMessage>() {
            @Override
            public Class messageType() {
                return RaceStartMessage.class;
            }

            @Override
            public void onRecieve(RaceStartMessage message) {
                System.out.println("Race Started!!!!!!!!!!!!!!!!!!!!!! FUJEA");
            }
        });

        this.registerMessage(new ActorMessage<SensorEvent>() {
            @Override
            public Class messageType() {
                return SensorEvent.class;
            }

            @Override
            public void onRecieve(SensorEvent message) {

                getPilot().tell(new PowerAction(100), getSelf());

            }
        });

        this.registerMessage(new ActorMessage<RoundTimeMessage>() {
            @Override
            public Class messageType() {
                return RoundTimeMessage.class;
            }

            @Override
            public void onRecieve(RoundTimeMessage message) {
                System.out.println("Change cars");
                handler.actors.get("StartRacer").stopWork();
                handler.actors.get("FastRacer").startWork();
            }
        });
    }

}
