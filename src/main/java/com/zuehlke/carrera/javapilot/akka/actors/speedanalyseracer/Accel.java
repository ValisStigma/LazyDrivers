package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;

import java.util.SortedMap;
import java.util.TreeMap;


public class Accel {

    private boolean dEqual(double a, double b){
        return Math.abs(a-b) < 0.00001;
    }

    public SortedMap<Long, Double> values = new TreeMap<>();

    double lastAccelSpeed = 0;
    double lastAccelTime = 0;

    double lastAccelLazyTime = 0;
    double lastAccelLazySpeed = 0;

    LazyActor actor;

    public Accel(LazyActor actor){
        this.actor = actor;
        actor.registerMessage(new ActorMessage<VelocityMessage>(VelocityMessage.class){
            @Override
            public void onRecieve(VelocityMessage message) {
                lastAccelSpeed = message.getVelocity();
                lastAccelTime = message.getTimeStamp();
            }
        });
    }

    public double getAccel(double timeStamp){
        double ret = 0;
        for (long time:values.keySet()){
            if (values.get(time) < timeStamp){
                ret = values.get(time);
            }
        }
        return 0; //TODO return ret;
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

    public double getMinorDistance(double endTime){
        return getMinorDistance(lastAccelSpeed, lastAccelTime, endTime);
    }

    public double getVelocity(double startSpeed, double startTime, double endTime){
        UnivariateIntegrator integrator = new SimpsonIntegrator();
        double integralAddition = 0;
        if (!dEqual(startTime,endTime)) {
            try {
                integralAddition = startTime * integrator.integrate(10, v -> getAccel(v), startTime, endTime);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return startSpeed + integralAddition;
    }

    public double getVelocity(double endTime){
        return getVelocity(lastAccelSpeed, lastAccelTime, endTime);
    }

    public double getDistance(double startSpeed, double startTime, double endTime){
        UnivariateIntegrator integrator = new SimpsonIntegrator();
        double integralAddition = 0;
        double integralSubtraction = 0;
        if (!dEqual(startTime,endTime)) {
            try {
                integralAddition = startTime * integrator.integrate(10, v -> getAccel(v), startTime, endTime);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            try {
                integralSubtraction = integrator.integrate(10, v -> v * getAccel(v), startTime, endTime);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        return startSpeed*(endTime-startTime) + integralAddition - integralSubtraction;
    }

    public double getDistance(double endTime){
        return getDistance(lastAccelSpeed, lastAccelTime, endTime);
    }

    public double getLazyDistance(double endSpeed, double endTime){
        double ret = ((endSpeed + lastAccelLazySpeed) / 2) * (endTime - lastAccelLazyTime);
        if (!dEqual(endTime, lastAccelLazyTime)){
            lastAccelLazySpeed = endSpeed;
            lastAccelLazyTime = endTime;
        }
        return ret;
    }
}