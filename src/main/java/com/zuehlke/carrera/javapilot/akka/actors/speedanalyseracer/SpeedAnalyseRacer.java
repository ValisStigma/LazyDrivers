package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;


public class SpeedAnalyseRacer extends LazyActor{

    private ActorHandler handler;

    private double lastSpeed = 0;
    private double currentSpeed = 0;
    private long lastTimeStamp = 0L;

    public SpeedAnalyseRacer(ActorHandler handler){
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                if(message.getTimeStamp()-lastTimeStamp > 40){
                    
                }
                lastTimeStamp = message.getTimeStamp();
                currentSpeed += (message.getA()[0] * (9.8/256));
            }
        });

        this.registerMessage(new ActorMessage<VelocityMessage>(VelocityMessage.class) {
            @Override
            public void onRecieve(VelocityMessage message) {
                System.out.println("Speed Diff: " + (message.getVelocity()-lastSpeed));

                currentSpeed = lastSpeed = message.getVelocity();
            }
        });
    }


}
