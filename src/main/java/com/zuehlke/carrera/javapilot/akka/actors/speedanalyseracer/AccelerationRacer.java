package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;


public class AccelerationRacer {

    private double lastSpeed = 0;
    private double lastTime = 0;
    private double currentSpeed = 0;

    private LazyActor actor;
    Accel accel;

    public AccelerationRacer(LazyActor actor, Accel accel){
        this.accel = accel;
        this.actor = actor;
        actor.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                double acceleration = (message.getA()[0] * (9.8/256));
                currentSpeed += acceleration; //TODO CHECK!!! (Unit/LSB) -> 9.8 for G Force
                accel.values.put(message.getTimeStamp(), acceleration);

                sendLazyVelocity(message.getTimeStamp());
            }
        });

        actor.registerMessage(new ActorMessage<VelocityMessage>(VelocityMessage.class) {
            @Override
            public void onRecieve(VelocityMessage message) {
                System.out.println("Speed Diff: " + (message.getVelocity()-accel.getVelocity(lastSpeed,lastTime,message.getTimeStamp())));



                System.out.println("Distance Calculated:  "+accel.getLazyDistance(message.getVelocity(), message.getTimeStamp()));
                System.out.println("DISTANCE:  "+accel.getDistance(lastSpeed,lastTime,message.getTimeStamp()));

                currentSpeed = lastSpeed = message.getVelocity();
                lastTime = message.getTimeStamp();
            }
        });
    }


    private void sendLazyVelocity(long timeStamp){
        actor.getPilot().forward(new LazyVelocityMessage(timeStamp, currentSpeed), actor.getContext());
    }

}
