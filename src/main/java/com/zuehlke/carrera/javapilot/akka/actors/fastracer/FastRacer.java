package com.zuehlke.carrera.javapilot.akka.actors.fastracer;

import akka.actor.Props;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;

import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;

/**
 * Created by tobias on 11.10.2015.
 */
public class FastRacer extends LazyActor{

    public static Props props() {
        return Props.create(
                FastRacer.class, () -> new FastRacer());
    }


    FastRacer(){
        this.registerMessage(new ActorMessage<com.zuehlke.carrera.relayapi.messages.SensorEvent>() {
            @Override
            public Class messageType() {
                return com.zuehlke.carrera.relayapi.messages.SensorEvent.class;
            }

            @Override
            public void onRecieve(com.zuehlke.carrera.relayapi.messages.SensorEvent message) {

                getPilot().tell(new PowerAction(150), getSelf());

            }
        });

        this.registerMessage(new ActorMessage<RoundTimeMessage>() {
            @Override
            public Class messageType() {
                return RoundTimeMessage.class;
            }

            @Override
            public void onRecieve(RoundTimeMessage message) {
                System.out.println("Change cars--->>>>FUUUUUU");
            }
        });
    }


}
