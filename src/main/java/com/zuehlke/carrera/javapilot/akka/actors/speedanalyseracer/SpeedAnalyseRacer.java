package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.DirectionHistory;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.TrackDirection;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;

import com.zuehlke.carrera.timeseries.FloatingHistory;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;


public class SpeedAnalyseRacer extends LazyActor{

    private ActorHandler handler;

    private double lastSpeed = 0;
    private double currentSpeed = 0;
    private long lastTimeStamp = 0L;

    class Accel {

        public SortedMap<Long, Double> values = new TreeMap<>();

        double lastSpeed = 0;
        double lastTime = 0;

        public double getAccel(double timeStamp){
            double ret = 0;
            for (double time:values.keySet()){
                if (values.get(time) < timeStamp){
                    ret = values.get(time);
                }
            }
            return ret;
        }

        public double getAccelStamp(double timeStamp){
            double ret = 0;
            for (double time:values.keySet()){
                if (values.get(time) < timeStamp){
                    ret = time;
                }
            }
            return ret;
        }

        public double getMinorDistance(double startSpeed, double startTime, double endTime){
            SortedMap<Long, Double> nMap = values.subMap((long) getAccelStamp(startTime), (long) getAccelStamp(endTime));
            double acceleration = 0;
            for (double acc: nMap.values()){
                acceleration += acc;
            }
            return startSpeed * (endTime-startTime) + 0.5*acceleration*(endTime-startTime)*(endTime-startTime);
        }

        public double getDistance(double startSpeed, double startTime, double endTime){
            UnivariateIntegrator integrator = new TrapezoidIntegrator();
            return startSpeed*(startTime-endTime) + startTime * integrator.integrate(64, v -> getAccel(v), endTime, startTime) -
                    integrator.integrate(64, v -> v*getAccel(v), endTime, startTime);
        }

        public double getLazyDistance(double endSpeed, double endTime){
            double ret = ((endSpeed + lastSpeed) / 2) * (endTime - lastTime);
            lastSpeed = endSpeed;
            lastTime = endTime;
            return ret;
        }
    }

    private Accel accel = new Accel();


    public SpeedAnalyseRacer(ActorHandler handler){
        this.handler = handler;

        handleTrack();

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                if(message.getTimeStamp()-lastTimeStamp > 40){
                    
                }
                lastTimeStamp = message.getTimeStamp();
                double acceleration = (message.getA()[0] * (9.8/256));
                currentSpeed += acceleration; //TODO CHECK!!! (Unit/LSB) -> 9.8 for G Force
                accel.values.put(message.getTimeStamp(), acceleration);

                sendLazyVelocity(message.getTimeStamp());
            }
        });

        this.registerMessage(new ActorMessage<VelocityMessage>(VelocityMessage.class) {
            @Override
            public void onRecieve(VelocityMessage message) {
                System.out.println("Speed Diff: " + (message.getVelocity()-currentSpeed));

                currentSpeed = lastSpeed = message.getVelocity();

                System.out.println("Distance Calculated:  "+accel.getLazyDistance(message.getVelocity(), message.getTimeStamp()));
            }
        });
    }

    private void sendLazyVelocity(long timeStamp){
        getPilot().forward(new LazyVelocityMessage(timeStamp, currentSpeed), getContext());
    }







    public int numberTrackElements = 24;




    private ArrayList<TrackDirection> dirHistory = new ArrayList<>();

    private FloatingHistory historyZ = new FloatingHistory(5);
    private DirectionHistory.Direction lastDir = null;
    private double speed;
    private double time;
    private boolean status = false;

    public void handleTrack(){

        this.registerMessage(new ActorMessage<VelocityMessage>(VelocityMessage.class){
            @Override
            public void onRecieve(VelocityMessage message) {
                speed = message.getVelocity();
                time = message.getTimeStamp();
            }
        });


        ActorMessage<SensorEvent> racingPhase = new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {

            }
        };



        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                int current = message.getG()[2];
                historyZ.shift(current);
                double interpolatedVal = (int) ((historyZ.currentMean() + current) / 2);
                DirectionHistory.Direction currentDir = getDirection(interpolatedVal);

                if(!currentDir.equals(lastDir)){
                    dirHistory.add(new TrackDirection(lastDir, getDistance(message.getTimeStamp())));
                }

                System.out.println("Size of TrackHist: "+dirHistory.size());


                if (dirHistory.size() > (numberTrackElements * 4)){
                    ArrayList<DirectionHistory.Direction> initialRound = new ArrayList<>();
                    System.out.println();

                    for(int i = numberTrackElements/2; i < numberTrackElements/2 +numberTrackElements; i++){
                        initialRound.add(dirHistory.get(i).getType());

                        System.out.print(i + "  |  ");
                    }

                    System.out.println();

                    for(int n = 1; n < 3; n++){
                        System.out.println();
                        int counter = 0;
                        for (int i = (numberTrackElements/2 +numberTrackElements*n); i < numberTrackElements*4; i++){
                            if (counter<numberTrackElements) {
                                System.out.print(i + "+" + n + "  |  ");
                                if (!initialRound.get(counter)
                                        .equals(dirHistory.get(i).getType())) {
                                    status = true;
                                }
                                counter++;
                            }
                        }
                    }

                    if (status) {
                        System.out.println("FAILDE");
                    }else{
                        System.out.println("Matched!!!!!!!!!");
                        for (DirectionHistory.Direction dir: initialRound){
                            System.out.print(dir+ "   |  ");
                        }
                        System.out.println();
                    }

                }

                lastDir = currentDir;
            }
        });




    }


    private double getDistance(double timestamp){
        //return accel.getDistance(speed, time, timestamp);
        return 0;
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
