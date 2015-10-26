package com.zuehlke.carrera.javapilot.eventStorage.events;

public class SensorEvent extends Event{
    private long timeStamp;
    private int gyroZValue;

    public SensorEvent(com.zuehlke.carrera.relayapi.messages.SensorEvent sensorEvent) {
        this.gyroZValue = sensorEvent.getG()[2];
        this.timeStamp = sensorEvent.getTimeStamp();
    }

    public long getTimeStamp() {
        return timeStamp;
    }


    public int getGyroZValze() {
        return gyroZValue;
    }
}
