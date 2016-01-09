package com.zuehlke.carrera.javapilot.akka.actors.interpolationracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.boostracer.BoostRacer;
import com.zuehlke.carrera.javapilot.akka.actors.staticracer.StaticRacer;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.timeseries.FloatingHistory;

import java.util.ArrayList;

public class InterpolationRacer extends LazyActor{

    private final ActorHandler handler;

    private ArrayList<TrackDirection> dirHistory = new ArrayList<>();

    private FloatingHistory historyZ = new FloatingHistory(5);
    private DirectionHistory.Direction lastDir = null;

    public InterpolationRacer(ActorHandler handler){
        this.handler = handler;



        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                int current = message.getG()[2];
                historyZ.shift(current);
                double interpolatedVal = (int) ((historyZ.currentMean() + current) / 2);


                if(interpolatedVal < -750 && lastDir!= DirectionHistory.Direction.LEFT){
                    dirHistory.add(new TrackDirection(DirectionHistory.Direction.LEFT, 0)); //TODO DISTANCE
                }else if(interpolatedVal > 750 && lastDir!= DirectionHistory.Direction.RIGHT){
                    dirHistory.add(new TrackDirection(DirectionHistory.Direction.RIGHT, 0));
                }else if(lastDir!= DirectionHistory.Direction.STRAIGHT){
                    dirHistory.add(new TrackDirection(DirectionHistory.Direction.STRAIGHT, 0));
                }
            }
        });

    }


}
