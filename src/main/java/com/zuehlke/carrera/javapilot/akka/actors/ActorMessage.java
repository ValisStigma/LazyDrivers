package com.zuehlke.carrera.javapilot.akka.actors;

/**
 * Created by tobias on 11.10.2015.
 */
public interface ActorMessage<T> {
    Class messageType();

    void onRecieve(T message);
}
