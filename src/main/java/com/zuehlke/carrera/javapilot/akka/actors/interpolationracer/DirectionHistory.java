package com.zuehlke.carrera.javapilot.akka.actors.interpolationracer;


import java.util.ArrayList;

public class DirectionHistory {
    public enum Direction{
        LEFT, RIGHT, STRAIGHT
    }

    private ArrayList<Direction> directionHistory = new ArrayList<>();

    public boolean isFirst(Direction dir){
        if (!currentDirection().equals(dir)){
            return false;
        }
        return !dir.equals(historyDirection());
    }

    private Direction currentDirction;

    public void pushDirection(Direction dir){
        currentDirction = dir;
        directionHistory.add(dir);

        System.out.println(lastDirection().toString() + ", " + currentDirection().toString());
    }

    public Direction currentDirection(){
        return currentDirction;
    }

    public Direction lastDirection(){
        if (directionHistory.size() > 2)
            return directionHistory.get(directionHistory.size()-2);
        return null;
    }


    public Direction historyDirection(){
        if(directionHistory.size() < 15){
            return currentDirection();
        }
        Direction lastDir = directionHistory.get(directionHistory.size()-2);
        for(int i = directionHistory.size()-3; i >= (directionHistory.size() - 10); --i){
            //System.out.print(", " + i);
            if(!lastDir.equals(directionHistory.get(i))){
                return currentDirection();
            }
        }
        //System.out.println();
        return lastDir;
    }

}
