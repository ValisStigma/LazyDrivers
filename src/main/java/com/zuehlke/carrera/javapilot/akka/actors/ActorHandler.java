package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.actors.analyseracer.AnalyseRacer;
import com.zuehlke.carrera.javapilot.akka.actors.boostracer.BoostRacer;
import com.zuehlke.carrera.javapilot.akka.actors.staticracer.StaticRacer;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.DirectionHistory;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.InterpolationRacer;
import com.zuehlke.carrera.javapilot.akka.actors.startracer.StartRacer;

import java.util.HashMap;
import java.util.Map;

public class ActorHandler extends UntypedActor {

    public Map<Class, LazyActorRef> actors = new HashMap<>();

    public DirectionHistory directionHistory;

    public static Props props( ActorRef pilotActor ) {
        return Props.create(
                ActorHandler.class, () -> new ActorHandler(pilotActor));
    }

    private LazyActorRef create(LazyCreator<? extends  LazyActor> actorCreate){
        return new LazyActorRef(getContext().system().actorOf(LazyActor.create(actorCreate)));
    }

    public ActorHandler(ActorRef pilot){
        LazyActor.setData(pilot, this);
        directionHistory = new DirectionHistory();

        actors.put(StartRacer.class, create(() -> new StartRacer(this, StaticRacer.class)));
        actors.put(StaticRacer.class, create(() -> new StaticRacer(this)));
        actors.put(AnalyseRacer.class, create(() -> new AnalyseRacer(this)));
        actors.put(BoostRacer.class, create(() -> new BoostRacer(this)));
        actors.put(InterpolationRacer.class, create(() -> new InterpolationRacer(this, directionHistory)));


        actors.get(InterpolationRacer.class).startWork();
        actors.get(StartRacer.class).startWork();
        actors.get(AnalyseRacer.class).startWork();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        for(LazyActorRef actor: actors.values()){
            if(actor.isWorking()) {
                actor.actorRef.forward(o, getContext());
            }
        }
    }


}
