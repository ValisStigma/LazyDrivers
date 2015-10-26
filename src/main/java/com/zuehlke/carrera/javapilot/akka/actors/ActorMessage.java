package com.zuehlke.carrera.javapilot.akka.actors;

public abstract class ActorMessage<T> {

    private Class messageType;

    public Class getType(){
        return messageType;
    }

    public ActorMessage(Class messageType){
        this.messageType = messageType;
    }

    public abstract void onRecieve(T message);
}
