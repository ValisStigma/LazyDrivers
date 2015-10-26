package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;


import java.util.ArrayList;
import java.util.Collection;

public class LazyActor extends UntypedActor {

    private Collection<ActorMessage> messages = new ArrayList<>();

    private static ActorRef pilot = null;

    public static void setPilot(ActorRef pilot){
        LazyActor.pilot = pilot;
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
}
