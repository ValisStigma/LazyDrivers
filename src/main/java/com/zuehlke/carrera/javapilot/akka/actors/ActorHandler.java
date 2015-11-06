package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.PowerAction;
import com.zuehlke.carrera.javapilot.akka.actors.analyseracer.AnalyseRacer;
import com.zuehlke.carrera.javapilot.akka.actors.boostracer.BoostRacer;
import com.zuehlke.carrera.javapilot.akka.actors.powerhandler.PowerHandler;
import com.zuehlke.carrera.javapilot.akka.actors.proberacer.ProbeRacer;
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

        actors.put(PowerHandler.class, create(()->new PowerHandler(this)));


        actors.put(StartRacer.class, create(() -> new StartRacer(this, ProbeRacer.class)));
        actors.put(StaticRacer.class, create(() -> new StaticRacer(this)));
        actors.put(ProbeRacer.class, create(()-> new ProbeRacer(this)));
        actors.put(AnalyseRacer.class, create(() -> new AnalyseRacer(this)));
        actors.put(BoostRacer.class, create(() -> new BoostRacer(this)));
        actors.put(InterpolationRacer.class, create(() -> new InterpolationRacer(this, directionHistory)));


        actors.get(InterpolationRacer.class).startWork();
        actors.get(StartRacer.class).startWork();
        actors.get(AnalyseRacer.class).startWork();

        actors.get(StaticRacer.class).startWork();

        actors.get(PowerHandler.class).startWork();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        for(LazyActorRef actor: actors.values()){
            if(actor.isWorking()) {
                actor.actorRef.forward(o, getContext());
            }
        }
    }


    public int getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(int currentPower) {
        this.currentPower = currentPower;
    }
}
