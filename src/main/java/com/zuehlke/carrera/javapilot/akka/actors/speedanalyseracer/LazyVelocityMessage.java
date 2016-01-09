package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import org.springframework.data.annotation.Transient;

public class LazyVelocityMessage {
    private String racetrackId;
    private long timeStamp;
    private double velocity;
    private String sourceId;
    private int t;

    public LazyVelocityMessage() {
    }

    public LazyVelocityMessage(long timeStamp, double velocity) {
        this.racetrackId = null;
        this.timeStamp = timeStamp;
        this.velocity = velocity;
        this.sourceId = null;
    }

    public LazyVelocityMessage(String racetrackId, long timeStamp, double velocity, String sourceId) {
        this.racetrackId = racetrackId;
        this.timeStamp = timeStamp;
        this.velocity = velocity;
        this.sourceId = sourceId;
    }

    public int getT() {
        return this.t;
    }

    public String getRacetrackId() {
        return this.racetrackId;
    }

    public void setRacetrackId(String racetrackId) {
        this.racetrackId = racetrackId;
    }

    @Transient
    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getVelocity() {
        return this.velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public String toString() {
        return String.format("from %s at %d: velocity = %f.", new Object[]{this.racetrackId, Long.valueOf(this.timeStamp), Double.valueOf(this.velocity)});
    }

    public LazyVelocityMessage valueObject() {
        return new LazyVelocityMessage((String)null, this.timeStamp, this.velocity, this.sourceId);
    }

    public void offSetTime(long startTime) {
        this.t = (int)(this.timeStamp - startTime);
    }
}
