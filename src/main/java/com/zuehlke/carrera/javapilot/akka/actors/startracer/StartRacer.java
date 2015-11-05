package com.zuehlke.carrera.javapilot.akka.actors.startracer;


import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.RaceStartMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class StartRacer extends LazyActor {

    private ActorHandler handler;

    public StartRacer(ActorHandler handler, Class nextRacer){
        this.handler = handler;

        this.registerMessage(new ActorMessage<RaceStartMessage>(RaceStartMessage.class) {
            @Override
            public void onRecieve(RaceStartMessage message) {
                setPower(255);
            }
        });

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                System.out.println("Engine Started");
                handler.actors.get(StartRacer.class).stopWork();
                handler.actors.get(nextRacer).startWork();
            }
        });
    }

}