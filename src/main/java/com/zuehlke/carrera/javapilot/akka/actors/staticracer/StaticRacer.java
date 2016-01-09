package com.zuehlke.carrera.javapilot.akka.actors.staticracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;

import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;
import com.zuehlke.carrera.relayapi.messages.VelocityMessage;

import java.io.*;


public class StaticRacer extends LazyActor{

    private ActorHandler handler;

    int currentPower = 141;

    public StaticRacer(ActorHandler handler){
        this.handler = handler;

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            public void onRecieve(SensorEvent message) {
                System.out.println(message.toString());
                setPower(handler.getStartPower());
            }
        });

        this.registerMessage(new ActorMessage<VelocityMessage>(VelocityMessage.class) {
            @Override
            public void onRecieve(VelocityMessage message) {
                printToFile(""+message.getVelocity());
            }
        });

        this.registerMessage(new ActorMessage<RoundTimeMessage>(RoundTimeMessage.class) {
            public void onRecieve(RoundTimeMessage message) {

                //printToFile("Last Round Time "+message.getRoundDuration());
                //printToFile("----------------------------------------------------------------------------------------------------------");
                //printToFile("Round Start...");


            }
        });
    }


    void printToFile(String text){
        try {
            File file = new File("D:/Test/"+currentPower+".txt");
            if(file == null){
                System.out.println("adsfsadf");
            }
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            out.println(text);
            out.close();
        } catch (IOException e) {
        }
    }
}
