package com.zuehlke.carrera.javapilot.akka.actors.proberacer;



import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.DirectionHistory;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

public class ProbeRacer extends LazyActor {

    private ActorHandler handler;

    private int straightPower = 70;
    private int curvePower = 200;
    private int steps = 3;
    private int current = curvePower;

    public ProbeRacer(ActorHandler handler) {
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                if (handler.directionHistory.historyDirection().equals(DirectionHistory.Direction.LEFT) ||
                    handler.directionHistory.historyDirection().equals(DirectionHistory.Direction.RIGHT)){
                    if(current == curvePower) System.out.println();
                    current -= steps;
                    if(current < (straightPower*1.5)) current = (int)(straightPower*1.5);
                    System.out.print(", "+current);
                    setPower(current);
                }else {
                    current = curvePower;
                    setPower(straightPower);
                }
            }
        });
    }
}
