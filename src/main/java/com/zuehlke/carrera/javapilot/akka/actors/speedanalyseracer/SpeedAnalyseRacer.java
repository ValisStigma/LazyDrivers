package com.zuehlke.carrera.javapilot.akka.actors.speedanalyseracer;

import com.zuehlke.carrera.javapilot.akka.actors.ActorHandler;
import com.zuehlke.carrera.javapilot.akka.actors.ActorMessage;
import com.zuehlke.carrera.javapilot.akka.actors.LazyActor;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.DirectionHistory;
import com.zuehlke.carrera.javapilot.akka.actors.interpolationracer.TrackDirection;
import com.zuehlke.carrera.relayapi.messages.PenaltyMessage;
import com.zuehlke.carrera.relayapi.messages.SensorEvent;

import com.zuehlke.carrera.timeseries.FloatingHistory;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class SpeedAnalyseRacer extends LazyActor{

    private final AccelerationRacer accelerationRacer;
    private final ActorMessage<SensorEvent> preRacing;
    private ActorHandler handler;
    private Accel accel;



    /*

    CONFIG PART

     */






    private int customStartPower = 0;

    public int numberTrackParts = 17;
    public int numberLRSwitches = 3;


    private ArrayList<Integer> startPowerRaiseRacing = new ArrayList<Integer>(){{
        add(10);
        add(5);
        add(2);
    }};










    ///////////////////////////////////////////////////////////////////////////TODO END CONFIG

    private FloatingHistory gyr = new FloatingHistory(10);

    private int racingPowerStartCounter = 0;

    private boolean initializedRacingValues = false;
    private TrackDirection.State globalState = TrackDirection.State.INIT;

    private TrackDirection.State globalNextSate;

    private int globalMaxPower;

    public SpeedAnalyseRacer(ActorHandler handler){
        this.handler = handler;

        preRacing = new ActorMessage<SensorEvent>(SensorEvent.class){
            @Override
            public void onRecieve(SensorEvent message) {
                if(handler.getStartPower() != 0){
                    if(!startedRacingPhase) {
                        setPower(handler.getStartPower());
                    } else {
                        initializeRacing();
                    }
                }
            }
        };

        this.registerMessage(preRacing);

        accel = new Accel(this);
        accelerationRacer = new AccelerationRacer(this, accel);
        handleTrack();
    }

    private void initializeRacing() {
        if(!initializedRacingValues){
            int initValue = handler.getStartPower();
            if(customStartPower != 0){
                initValue = customStartPower;
            }
            for (int i = 0; i < currentPosition; i++){
                TrackDirection curele = dirHistory.get(i);
                curele.standartPower = initValue;
                curele.nextPower = initValue;
            }
            currentTrackElement.standartPower = initValue;
            currentTrackElement.nextPower = initValue;
            globalMaxPower = initValue;
            initializedRacingValues = true;
            System.out.println("initialized Track speed");
        }
    }



    private int numberTrackElements = numberLRSwitches+numberTrackParts;
    private int currentPosition = 0;
    private int currentPositionLastRound = 0;

    private CopyOnWriteArrayList<TrackDirection> dirHistory = new CopyOnWriteArrayList<>();
    private TrackDirection currentTrackElement;

    private FloatingHistory historyZ = new FloatingHistory(5);
    private DirectionHistory.Direction lastDir = null;

    private double lastTrackElementSpeed = 0;
    private double lastTrackElementTime = 0;

    private CopyOnWriteArrayList<TrackDirection> recoveryList = new CopyOnWriteArrayList<>();


    private int getRacingRaise(){
        int ret = 0;
        if (racingPowerStartCounter<startPowerRaiseRacing.size()){
            ret = startPowerRaiseRacing.get(racingPowerStartCounter);
        }else{
            globalNextSate = TrackDirection.State.RANDOM_RACE;
        }
        return ret;
    }

    private void updateRacingRaiseCounter(){
        racingPowerStartCounter++;
    }


    public void handleTrack(){

        this.registerMessage(new ActorMessage<SensorEvent>(SensorEvent.class) {
            @Override
            public void onRecieve(SensorEvent message) {
                gyr.shift(message.getG()[2]);
                int current = message.getG()[2];
                historyZ.shift(current);
                double interpolatedVal = (int) ((historyZ.currentMean() + current) / 2);
                DirectionHistory.Direction currentDir = getDirection(interpolatedVal);


                if (globalState == TrackDirection.State.RACE){
                    System.out.print(getPower() + " )- ");
                    for (int i = currentPositionLastRound; i < currentPosition; i++){
                        TrackDirection test = dirHistory.get(i);
                        if(!test.isLRSwitch)
                            System.out.print(test.getType() + " --> ");
                    }
                    System.out.println(generatePattern(currentPositionLastRound, currentPosition));

                    if (initializedRacingValues){
                        setPower(currentTrackElement.standartPower);
                    }
                }

                if (globalState == TrackDirection.State.RECOVER){
                    setPower(globalMaxPower);
                }


                if(!currentDir.equals(lastDir)){
                    System.out.println("History Size:  "+dirHistory.size()+"  driving at "+currentPosition);
                    //checkListIntegrity();


                    //TODO RECOVERY STATE
                    if (globalState == TrackDirection.State.RECOVER){
                        recovery(currentDir);
                    }


                    if (globalState != TrackDirection.State.RECOVER){
                        if (globalState == TrackDirection.State.RACE) {
                            if (nextRoundFinished()) {
                                updateGlobalPower();
                                updateNextRacingSpeed();
                                nextRoundUp();
                            }
                        }
                        initNextTrackElement(message, currentDir);
                    }
                }

                if (dirHistory.size() > (numberTrackElements * 3)){
                    if(checkListIntegrity()){
                        setLRSwitches();
                        startRacingPhase();
                    }
                }

                lastDir = currentDir;
            }
        });

        this.registerMessage(new ActorMessage<PenaltyMessage>(PenaltyMessage.class) {
            @Override
            public void onRecieve(PenaltyMessage message) {
                if (globalState != TrackDirection.State.RECOVER) {
                    recoverTrack();
                }
            }
        });
    }

    private void updateNextRacingSpeed() {
        for (int i = currentPositionLastRound; i < currentPosition; i++) {
            dirHistory.get(i).nextPower = dirHistory.get(i).standartPower + getRacingRaise();
            System.out.println("Raising power To: " + dirHistory.get(i).nextPower);
        }

        currentTrackElement.standartPower = dirHistory.get(currentPositionLastRound).standartPower + getRacingRaise();
        currentTrackElement.nextPower = currentTrackElement.standartPower;
    }

    private void updateGlobalPower() { //TODO MAYBE UPDATE
        if (currentPositionLastRound-numberTrackElements > numberTrackElements) {
            if (globalMaxPower < dirHistory.get(currentPositionLastRound - numberTrackElements).standartPower) {
                //globalMaxPower = dirHistory.get(currentPositionLastRound - numberTrackElements).standartPower;
            }
        }
    }

    private boolean nextRoundFinished() {
        return currentPosition >= racingRoundCounter;
    }

    private void nextRoundUp() {
        racingRoundCounter += numberTrackElements;
    }

    private void recovery(DirectionHistory.Direction currentDir) {
        if (!isMoving()){
            resetRecovery();
        }
        System.out.println("Recovering "+recoveryList.size()+"    " + currentDir);
        recoveryList.add(new TrackDirection(lastDir, 0));
        if (recoveryList.size() > numberTrackElements+1){
            String hay = generatePattern(0, currentPosition);
            String pattern = generatePattern(recoveryList.size()-numberTrackElements, recoveryList.size(), recoveryList);
            setCurrentPos(hay.lastIndexOf(pattern)-1);

            System.out.println("Recovered at:  "+currentPosition+"   of   "+dirHistory.size()+ "  with "+pattern);

            currentTrackElement = dirHistory.get(currentPosition);

            globalState = globalNextSate;
        }
    }

    private void resetLRPos() {
        currentPositionLastRound = currentPosition - numberTrackElements;
    }

    private void setCurrentPos(int newPos){
        currentPosition = newPos;
        resetLRPos();
    }

    private boolean isMoving(){
        return gyr.currentStDev() >= 10;
    }

    /*private void forwardCorrectPosition() {
        if (gyr.currentStDev() >= 10) {
            System.out.println("Recovering "+ recoveryPattern +"  from "+currentPosition+" with "+dirHistory.size());
            if (currentPosition < 1) {
                setCurrentPos(1);
            }
            String pattern = generatePattern(currentPosition, dirHistory.size());
            while (pattern.indexOf(recoveryPattern) != 0){
                System.out.println("Matching "+ pattern+ " with " +recoveryPattern);

                setCurrentPos(currentPosition+1);
                if (currentPosition>dirHistory.size()){
                    currentPosition = numberTrackElements * 4;
                    System.out.println("Reseting match");
                }
                pattern = generatePattern(currentPosition, dirHistory.size());
            }

            for(int i = 0; i < dirHistory.size(); i++){
                //dirHistory.get(i).nextPower = dirHistory.get(i).standartPower;
            }

            System.out.println("Recovered State to "+currentPosition+"  of available "+dirHistory.size());
            currentTrackElement = dirHistory.get(currentPosition);
            globalState = TrackDirection.State.RACE;
        }
    }*/

    private void recoverTrack(){
        resetRecovery();
        updateRacingRaiseCounter();
    }

    private void resetRecovery(){
        globalState = TrackDirection.State.RECOVER;
        recoveryList = new CopyOnWriteArrayList<>();
    }

    private String generatePattern(int start, int endPlus, List<TrackDirection> list){
        String a = "";
        for(int i = start; i < endPlus; i++){
            TrackDirection currentPart = list.get(i);
            if (currentPart.getType() == DirectionHistory.Direction.RIGHT){
                a += "R";
            }else if (currentPart.getType() == DirectionHistory.Direction.LEFT){
                a += "L";
            }else{
                a += "S";
            }
        }
        return a;
    }

    private String generatePattern(int start, int endPlus){
        return generatePattern(start,endPlus,dirHistory);
    }

    private void initNextTrackElement(SensorEvent message, DirectionHistory.Direction currentDir) {
        if(currentTrackElement != null) {
            currentTrackElement.setDistance(accel.getDistance(lastTrackElementSpeed,lastTrackElementTime,message.getTimeStamp()));
            setOrAdd(currentPosition, currentTrackElement);
            setCurrentPos(currentPosition+1);

        }
        lastTrackElementSpeed = accel.getVelocity(message.getTimeStamp());
        lastTrackElementTime = message.getTimeStamp();
        currentTrackElement = new TrackDirection(currentDir, 0);
        if (currentPositionLastRound > 0) {
            TrackDirection lastEle = dirHistory.get(currentPositionLastRound);
            currentTrackElement.isLRSwitch = lastEle.isLRSwitch;
            currentTrackElement.standartPower = lastEle.nextPower;
            System.out.println("New Elem Power++"+lastEle.nextPower);
            currentTrackElement.nextPower = lastEle.nextPower;
        }
        currentTrackElement.startTime = lastTrackElementTime;
        currentTrackElement.startSpeed = lastTrackElementSpeed;
    }

    private void setOrAdd(int index, TrackDirection dir){
        if (dirHistory.size()>index){
            dirHistory.set(index, dir);
            return;
        }
        dirHistory.add(dir);
    }


    private boolean startedRacingPhase = false;

    private int racingRoundCounter = 0;

    private void startRacingPhase() {


        if (!startedRacingPhase) {
            racingRoundCounter = currentPosition + numberTrackElements;

            globalNextSate = TrackDirection.State.RACE;
            globalState = TrackDirection.State.RACE;
            System.out.println("Started Racing");

            startedRacingPhase = true;
        }
    }

    private boolean checkTrackInitLayout() {
        ArrayList<DirectionHistory.Direction> initialRound = new ArrayList<>();
        for(int i = numberTrackElements/2; i < numberTrackElements/2 +numberTrackElements; i++){
            initialRound.add(dirHistory.get(i).getType());
        }
        for(int n = 1; n < 3; n++){
            int counter = 0;
            for (int i = (numberTrackElements/2 +numberTrackElements*n); i < numberTrackElements*4; i++){
                if (counter<numberTrackElements) {
                    if (!initialRound.get(counter)
                            .equals(dirHistory.get(i).getType())) {
                        return false;
                    }
                    counter++;
                }
            }
        }



        return true;
    }


    private boolean LRSwitchesSet = false;
    private void setLRSwitches() {
        if(numberLRSwitches > 0 && !LRSwitchesSet) {
            LRSwitchesSet = true;
            TreeMap<Double, Integer> distMap = new TreeMap<>();

            for (int i = currentPositionLastRound; i < currentPosition; i++) {
                distMap.put(dirHistory.get(i).getDistance(), i);
            }
            int ctr = 0;
            for (int i: distMap.values()){
                if(ctr < numberLRSwitches){
                    if (dirHistory.get(i).getType()== DirectionHistory.Direction.STRAIGHT) {
                        for (int j = i; j >= 0; j -= numberTrackElements) {
                            dirHistory.get(j).isLRSwitch = true;
                        }
                        ctr++;
                    }
                }else {
                    break;
                }
            }
        }

    }

    private boolean checkListIntegrity(){
        if (currentPositionLastRound > 0) {
            for (int i = currentPositionLastRound; i < currentPosition; i++) {
                TrackDirection test = dirHistory.get(i);
                for (int j = i; j >= 0; j -= numberTrackElements) {
                    if (test.getType() != dirHistory.get(j).getType()) {
                        throw new NullPointerException("List Check Failed On " + i + " beeing " + test.getType() + " and " + j + " beeing " + dirHistory.get(j).getType());
                    }
                }
            }
        }
        return true;
    }

    private DirectionHistory.Direction getDirection(double interpolatedVal){
        if(interpolatedVal < -1000){
            return DirectionHistory.Direction.LEFT;
        }else if(interpolatedVal > 1000){
            return DirectionHistory.Direction.RIGHT;
        }else{
            return DirectionHistory.Direction.STRAIGHT;
        }
    }

}
