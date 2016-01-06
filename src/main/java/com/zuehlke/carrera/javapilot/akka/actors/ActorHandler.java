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

import java.util.HashMap;
import java.util.Map;

public class ActorHandler extends UntypedActor {

    public Map<Class, LazyActorRef> actors = new HashMap<>();

    public DirectionHistory directionHistory;

    private ActorRef pilot;

    private int currentPower = 255;

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
        LazyActor.setData(this.pilot, this);
        directionHistory = new DirectionHistory();

        create(PowerHandler.class, ()->new PowerHandler(this)).startWork();

        create(SpeedAnalyseRacer.class, ()->new SpeedAnalyseRacer(this)).startWork();

        create(StartRacer.class, () -> new StartRacer(this, StaticRacer.class)).startWork();
        create(StaticRacer.class, () -> new StaticRacer(this));
        create(ProbeRacer.class, ()-> new ProbeRacer(this));
        create(AnalyseRacer.class, () -> new AnalyseRacer(this)).startWork();
        create(BoostRacer.class, () -> new BoostRacer(this));
        create(InterpolationRacer.class, () -> new InterpolationRacer(this, directionHistory)).startWork();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        actors.values().stream().filter(actor -> actor.isWorking()).forEach(actor -> {
            actor.actorRef.forward(o, getContext());
        });
    }


    public int getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }
}
