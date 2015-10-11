package com.zuehlke.carrera.javapilot.analysis;

public class ElementIdentifier {
    public enum TrackElement {
        LeftCurve {
            @Override
            public String toString() {
                return "L";
            }
        },
        RightCurve{
            @Override
            public String toString() {
                return "R";
            }
        },
        Straight{
            @Override
            public String toString() {
                return "S";
            }
        }
    }

    private int threshHoldLeft;
    private int threshHoldRight;


    public ElementIdentifier(final int threshHoldLeft, final int threshHoldRight) {
        this.threshHoldLeft = threshHoldLeft;
        this.threshHoldRight = threshHoldRight;
    }

    public TrackElement identify(final int gyroZValue) {
        if(gyroZValue <= threshHoldLeft) {
            return TrackElement.LeftCurve;
        } else if(gyroZValue <= threshHoldRight) {
            return TrackElement.Straight;
        } else {
            return TrackElement.RightCurve;
        }
    }
}
