package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;

public class LazyActorRef {

    public final ActorRef actorRef;
    public LazyActor actor = null;


    protected LazyActorRef(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    private boolean working = false;

    public boolean isWorking(){
        return working;
    }

    public void startWork(){
        working = true;
    }

    public void stopWork(){
        working = false;
    }

}
