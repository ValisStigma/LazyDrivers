package com.zuehlke.carrera.javapilot.akka.actors.interpolationracer;


public class TrackDirection {

    private DirectionHistory.Direction type = DirectionHistory.Direction.STRAIGHT;


    public enum State{
        RACE, RECOVER, SATISFIED, INIT, STRAIGHT_RACE, CURVE_RACE, START_PROBE
    }

    public State state = State.RACE;

    public boolean isLRSwitch;

    public int standartPower = 0;
    public int nextPower = 0;

    public int curvePowerUp = 0;

    public int raisedStraightPower = 0;
    public long millisRaisedStraight = 0;

    //public double percentage = 0.5; //TODO MAYBE
    //public double percentagePower = 0;



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
