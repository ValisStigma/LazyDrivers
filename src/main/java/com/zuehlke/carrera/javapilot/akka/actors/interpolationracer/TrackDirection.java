package com.zuehlke.carrera.javapilot.akka.actors.interpolationracer;


public class TrackDirection {

    private DirectionHistory.Direction type = DirectionHistory.Direction.STRAIGHT;


    public boolean isLRSwitch;

    public int standartPower = 0;



    private double distance;

    public double startSpeed;
    public double startTime;


    public TrackDirection(DirectionHistory.Direction dir, double distance){
        type = dir;
        this.distance = distance;
    }


    public DirectionHistory.Direction getType() {
        return type;
    }

    public void setType(DirectionHistory.Direction type) {
        this.type = type;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
