package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.DirectionHistory;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.TrackDirection;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

import com.zuehlke.carrera.timeseries.FloatingHistory;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class SpeedAnalyseRacer extends LazyActor{

    private final AccelerationRacer accelerationRacer;
    private final ActorMessage<SensorEvent> preRacing;
    private ActorHandler handler;
    private Accel accel;


    public SpeedAnalyseRacer(ActorHandler handler){
        this.handler = handler;

        preRacing = new ActorMessage<SensorEvent>(SensorEvent.class){
            @Override
            public void onRecieve(SensorEvent message) {
                if(handler.getStartPower() != 0){
                    if(!startedRacingPhase) {
                        setPower(handler.getStartPower());
                    }
                }
            }
        };

        this.registerMessage(preRacing);

        accel = new Accel(this);
        accelerationRacer = new AccelerationRacer(this, accel);
        handleTrack();
    }

    public int numberTrackParts = 17;
    public int numberLRSwitches = 3;



    private int numberTrackElements = numberLRSwitches+numberTrackParts;
    private int currentPosition = 0;
    private int currentPositionLastRound = 0;

    private CopyOnWriteArrayList<TrackDirection> dirHistory = new CopyOnWriteArrayList<>();
    private TrackDirection currentTrackElement;

    private FloatingHistory historyZ = new FloatingHistory(5);
    private DirectionHistory.Direction lastDir = null;

    private double lastTrackElementSpeed = 0;
    private double lastTrackElementTime = 0;

    public void handleTrack(){

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                int current = message.getG()[2];
                historyZ.shift(current);
                double interpolatedVal = (int) ((historyZ.currentMean() + current) / 2);
                DirectionHistory.Direction currentDir = getDirection(interpolatedVal);

                if(!currentDir.equals(lastDir)){
                    initNextTrackElement(message, currentDir);
                }

                if (dirHistory.size() > (numberTrackElements * 4)){
                    if(checkTrackInitLayout()){
                        startRacingPhase();
                    }
                }

                lastDir = currentDir;
            }
        });
    }

    private void initNextTrackElement(SensorEvent message, DirectionHistory.Direction currentDir) {
        if(currentTrackElement != null) {
            currentTrackElement.setDistance(accel.getDistance(lastTrackElementSpeed,lastTrackElementTime,message.getTimeStamp()));
            setOrAdd(currentPosition, currentTrackElement);
            currentPosition++;
            currentPositionLastRound = currentPosition-numberTrackElements;
        }
        lastTrackElementSpeed = accel.getVelocity(message.getTimeStamp());
        lastTrackElementTime = message.getTimeStamp();
        currentTrackElement = new TrackDirection(currentDir, 0);
        if (currentPositionLastRound > 0) {
            currentTrackElement.isLRSwitch = dirHistory.get(currentPositionLastRound).isLRSwitch;
        }
        currentTrackElement.startTime = lastTrackElementTime;
        currentTrackElement.startSpeed = lastTrackElementSpeed;
    }

    private void setOrAdd(int index, TrackDirection dir){
        if (dirHistory.size()>index){
            dirHistory.set(index, dir);
            return;
        }
        dirHistory.add(dir);
    }


    private boolean startedRacingPhase = false;
    private void startRacingPhase() {

        ActorMessage<SensorEvent> racingPhase = new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                for (int i = currentPositionLastRound; i < currentPosition; i++){
                    TrackDirection test = dirHistory.get(i);
                /*
                    if(i==currentPositionLastRound){
                        System.out.print("Dist: " +
                                (accel.getDistance(test.startSpeed,test.startTime,message.getTimeStamp()) /
                                        test.getDistance() * 100.0) +"%");
                    }
                */
                    if(!test.isLRSwitch)
                        System.out.print(test.getType() + " --> ");
                }
                System.out.println();
            }
        };
        if (!startedRacingPhase) {
            System.out.println("Started Racing");
            this.registerMessage(racingPhase);
            startedRacingPhase = true;
        }
    }

    private boolean checkTrackInitLayout() {
        ArrayList<DirectionHistory.Direction> initialRound = new ArrayList<>();
        for(int i = numberTrackElements/2; i < numberTrackElements/2 +numberTrackElements; i++){
            initialRound.add(dirHistory.get(i).getType());
        }
        for(int n = 1; n < 3; n++){
            int counter = 0;
            for (int i = (numberTrackElements/2 +numberTrackElements*n); i < numberTrackElements*4; i++){
                if (counter<numberTrackElements) {
                    if (!initialRound.get(counter)
                            .equals(dirHistory.get(i).getType())) {
                        return false;
                    }
                    counter++;
                }
            }
        }

        setLRSwitches();

        return true;
    }

    private void setLRSwitches() {
        if(numberLRSwitches > 0) {
            TreeMap<Double, Integer> distMap = new TreeMap<>();

            for (int i = currentPositionLastRound; i < currentPosition; i++) {
                distMap.put(dirHistory.get(i).getDistance(), i);
            }
            int ctr = 0;
            for (int i: distMap.values()){
                if(ctr < numberLRSwitches){
                    for (int j = i; j >= 0; j -= numberTrackElements){
                        dirHistory.get(j).isLRSwitch = true;
                    }
                }else {
                    break;
                }
                ctr++;
            }
        }

    }

    private DirectionHistory.Direction getDirection(double interpolatedVal){
        if(interpolatedVal < -750){
            return DirectionHistory.Direction.LEFT;
        }else if(interpolatedVal > 750){
            return DirectionHistory.Direction.RIGHT;
        }else{
            return DirectionHistory.Direction.STRAIGHT;
        }
    }

}
