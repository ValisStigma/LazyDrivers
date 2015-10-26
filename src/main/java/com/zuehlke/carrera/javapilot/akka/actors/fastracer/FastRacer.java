package com.zuehlke.carrera.javapilot.akka.actors.fastracer;

import akka.actor.Props;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;

import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;


public class FastRacer extends LazyActor{

    private ActorHandler handler;

    public static Props props(ActorHandler handler) {
        return Props.create(
                FastRacer.class, () -> new FastRacer(handler));
    }


    FastRacer(ActorHandler handler){
        this.handler = handler;
        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            public void onRecieve(SensorEvent message) {
                getPilot().tell(new PowerAction(150), getSelf());
            }
        });

        this.registerMessage(new ActorMessage<RoundTimeMessage>(RoundTimeMessage.class) {
            public void onRecieve(RoundTimeMessage message) {
                handler.actors.get("FastRacer").stopWork();
                handler.actors.get("StartRacer").startWork();
                System.out.println("Change cars--->>>>FUUUUUU");
            }
        });
    }


}
