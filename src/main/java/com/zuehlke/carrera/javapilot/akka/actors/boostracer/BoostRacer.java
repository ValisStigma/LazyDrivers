package com.zuehlke.carrera.javapilot.akka.actors.boostracer;


import akka.actor.Props;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.staticracer.StaticRacer;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class BoostRacer extends LazyActor{

    private ActorHandler handler;
    private int counter = 1;

    private int boostPower = 250;
    private int boostTimes = 10;

    public BoostRacer(ActorHandler handler) {
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                if(handler.directionHistory.currentDirection().equals(handler.directionHistory.lastDirection())){
                    if(counter == 0) {
                        handler.actors.get(StaticRacer.class).startWork();
                        handler.actors.get(BoostRacer.class).stopWork();
                        System.out.println("Boosted");
                    }
                    counter = (counter + 1) % boostTimes;
                    setPower(boostPower);
                }
            }
        });
    }

    public static Props props(ActorHandler handler) {
        return Props.create(
                BoostRacer.class, () -> new BoostRacer(handler));
    }
}
