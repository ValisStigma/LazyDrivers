package com.zuehlke.carrera.javapilot.akka.actors.interpolationracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.boostracer.BoostRacer;
import com.zuehlke.carrera.javapilot.akka.actors.staticracer.StaticRacer;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.timeseries.FloatingHistory;

public class InterpolationRacer extends LazyActor{

    private final ActorHandler handler;

    private DirectionHistory dirHistory = new DirectionHistory();

    private FloatingHistory historyZ = new FloatingHistory(5);

    public InterpolationRacer(ActorHandler handler, DirectionHistory history){
        this.handler = handler;
        this.dirHistory = history;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                int current = message.getG()[2];
                historyZ.shift(current);
                double interpolatedVal = (int) ((historyZ.currentMean() + current) / 2);

                if(interpolatedVal < -1000){
                    dirHistory.pushDirection(DirectionHistory.Direction.LEFT);
                }else if(interpolatedVal > 1000){
                    dirHistory.pushDirection(DirectionHistory.Direction.RIGHT);
                }else{
                    dirHistory.pushDirection(DirectionHistory.Direction.STRAIGHT);
                }

                if (dirHistory.isFirst(DirectionHistory.Direction.STRAIGHT)){
                    //handler.actors.get(StaticRacer.class).stopWork();
                    //handler.actors.get(BoostRacer.class).startWork();
                }
            }
        });

    }


}
