package com.zuehlke.carrera.javapilot.akka.actors.startracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.timeseries.FloatingHistory;

public class StartRacer extends LazyActor {

    private ActorHandler handler;
    private int startPower = 0;

    private FloatingHistory gyr = new FloatingHistory(20);

    public StartRacer(ActorHandler handler, Class nextRacer){
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                gyr.shift(message.getG()[2]);
                if (gyr.currentStDev() < 10) {
                    startPower += 1;
                    setPower(startPower);
                }else{
                    handler.setStartPower(startPower);
                    handler.actors.get(StartRacer.class).stopWork();
                    handler.actors.get(nextRacer).startWork();
                }
            }
        });
    }

}