package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class LazyActor extends UntypedActor {

    private CopyOnWriteArrayList<ActorMessage> messages = new CopyOnWriteArrayList<>();

    private static ActorRef pilot;
    private static ActorHandler handler;

    public static void setData(ActorRef pilot, ActorHandler handler){
        LazyActor.pilot = pilot;
        LazyActor.handler = handler;
    }

    public void registerMessage(ActorMessage message){
        messages.add(message);
    }

    public void unregisterMessage(ActorMessage message){
        messages.remove(message);
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        for(ActorMessage message: messages){
            if(message.getType().isInstance(msg)){
                message.onRecieve(msg);
            }
        }
    }

    public ActorRef getPilot() {
        return LazyActor.pilot;
    }

    public int getPower() {
        return handler.getCurrentPower();
    }

    public void setPower(int currentPower) {
        handler.setCurrentPower(currentPower);
    }
}
