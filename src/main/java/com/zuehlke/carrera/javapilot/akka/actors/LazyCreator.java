package com.zuehlke.carrera.javapilot.akka.actors;

public interface LazyCreator<T> {
    public T create();
}
