package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;


import java.util.ArrayList;

public class LazyActor extends UntypedActor {

    private ArrayList<ActorMessage> messages = new ArrayList<>();

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
            if(msg.getClass().equals(message.getType())){
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
