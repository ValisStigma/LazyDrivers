package com.zuehlke.carrera.javapilot.config;

/**
 * Created by rafael on 11.10.2015.
 */
public class BaseTrackConfig {
    private static int leftThreshHold = -500;
    private static int rightThreshHold = 500;
    public static int getLeftThreshHold() {
        return leftThreshHold;
    }
    public static int getRightThreshHold() {
        return rightThreshHold;
    }

}
