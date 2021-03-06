package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.actors.analyseracer.AnalyseRacer;
import com.zuehlke.carrera.javapilot.akka.actors.boostracer.BoostRacer;
import com.zuehlke.carrera.javapilot.akka.actors.powerhandler.PowerHandler;
import com.zuehlke.carrera.javapilot.akka.actors.proberacer.ProbeRacer;
import com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer.SpeedAnalyseRacer;
import com.zuehlke.carrera.javapilot.akka.actors.staticracer.StaticRacer;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.DirectionHistory;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.InterpolationRacer;
import com.zuehlke.carrera.javapilot.akka.actors.startracer.StartRacer;
import com.zuehlke.carrera.relayapi.messages.RaceStartMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

import java.util.HashMap;
import java.util.Map;

public class ActorHandler extends UntypedActor {

    public Map<Class, LazyActorRef> actors;

    public DirectionHistory directionHistory;

    private ActorRef pilot;

    private int currentPower = 0;
    private int startPower = 0;
    private long raceStartTime = 0;

    public static Props props( ActorRef pilotActor ) {
        return Props.create(
                ActorHandler.class, () -> new ActorHandler(pilotActor));
    }

    private LazyActorRef create(Class c, LazyCreator<? extends LazyActor> newClass){
        LazyActorRef temp = create(newClass);
        actors.put(c, temp);
        return temp;
    }

    private LazyActorRef create(LazyCreator<? extends LazyActor> newClass){
        return new LazyActorRef(getContext().system().actorOf(Props.create(LazyActor.class, () -> {
            LazyActor actor = newClass.create();
            actors.get(actor.getClass()).actor = actor;
            return actor;
        })));
    }

    public ActorHandler(ActorRef pilot){
        this.pilot = pilot;
        init();
    }

    private void init() {
        actors = new HashMap<>();

        LazyActor.setData(this.pilot, this);
        directionHistory = new DirectionHistory();

        create(PowerHandler.class, ()->new PowerHandler(this)).startWork();

        create(SpeedAnalyseRacer.class, ()->new SpeedAnalyseRacer(this)).startWork();

        create(StartRacer.class, () -> new StartRacer(this, ProbeRacer.class)).startWork();
        create(StaticRacer.class, () -> new StaticRacer(this));
        create(ProbeRacer.class, ()-> new ProbeRacer(this));
        create(AnalyseRacer.class, () -> new AnalyseRacer(this)).startWork();
        create(BoostRacer.class, () -> new BoostRacer(this));
        create(InterpolationRacer.class, () -> new InterpolationRacer(this, directionHistory)).startWork();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof RaceStartMessage){
            raceStartTime = ((RaceStartMessage) o).getTimestamp();
        }
        if (raceStartTime == 0){
            if (o instanceof SensorEvent){
                raceStartTime = ((SensorEvent) o).getTimeStamp();
            }
        }

        actors.values().stream().filter(actor -> actor.isWorking()).forEach(actor -> {
            actor.actorRef.forward(o, getContext());
            //System.out.println("fw to : "+actor.actor.getClass().toString());
        });
    }


    public int getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }

    public int getStartPower() {
        return startPower;
    }

    public void setStartPower(int startPower) {
        this.startPower = startPower;
    }

    public long getRaceStartTime() {
        return raceStartTime;
    }

    public void setRaceStartTime(long raceStartTime) {
        this.raceStartTime = raceStartTime;
    }
}
