package com.zuehlke.carrera.javapilot.akka.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.zuehlke.carrera.javapilot.akka.actors.analyseracer.AnalyseRacer;
import com.zuehlke.carrera.javapilot.akka.actors.fastracer.FastRacer;
import com.zuehlke.carrera.javapilot.akka.actors.startracer.StartRacer;

import java.util.HashMap;
import java.util.Map;

public class ActorHandler extends UntypedActor {

    public Map<String, LazyActorRef> actors = new HashMap<>();

    public static Props props( ActorRef pilotActor ) {
        return Props.create(
                ActorHandler.class, () -> new ActorHandler(pilotActor));
    }

    public ActorHandler(ActorRef pilot){
        LazyActor.setPilot(pilot);

        actors.put("StartRacer", new LazyActorRef(getContext().system().actorOf(StartRacer.props(this))));
        actors.put("FastRacer", new LazyActorRef(getContext().system().actorOf(FastRacer.props(this))));
        actors.put("AnalyseRacer", new LazyActorRef(getContext().system().actorOf(AnalyseRacer.props(this))));



        actors.get("StartRacer").startWork();
        actors.get("AnalyseRacer").startWork();
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
