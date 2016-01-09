package com.zuehlke.carrera.javapilot.akka.actors.interpolationracer;

/**
 * Created by tobias on 09.01.2016.
 */
public class TrackDirection {

    private DirectionHistory.Direction type = DirectionHistory.Direction.STRAIGHT;

    private double distance;

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
