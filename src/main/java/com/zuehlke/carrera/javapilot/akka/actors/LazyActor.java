package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;


import java.util.ArrayList;
import java.util.Collection;

public class LazyActor extends UntypedActor {

    private Collection<ActorMessage> messages = new ArrayList<>();

    private static ActorRef pilot = null;
    private static ActorHandler handler = null;

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

    public static Props create(LazyCreator<? extends LazyActor> newClass){
        return Props.create(LazyActor.class, () -> {
            LazyActor actor = newClass.create();
            handler.actors.get(actor.getClass()).actor = actor;
            return actor;
        });
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
}
