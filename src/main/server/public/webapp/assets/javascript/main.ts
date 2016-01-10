
/// <reference path="Typefiles/smoothie.d.ts"/>
/// <reference path="Typefiles/jquery.d.ts"/>
module MyModule {

    export interface Displayable {
        display():void;
        datapoint:any;
        timestamp:any;
    }

    export interface Display {
        add(displayable: Displayable):void;
        clear(): void;
    }

    export class RoundTimeDisplay implements Display {
        add(displayable:RoundTimeEvent):void {
            this.divContainer.append(displayable.element);
        }

        clear():void {
            this.divContainer.html('');
        }
        divContainer: JQuery;

        constructor(containerId:String) {
            this.divContainer = $('#' + containerId);
        }

    }
    export class TrackElementDisplay implements Display {
        add(displayable:TrackElement):void {
            this.displayTrackImage(displayable);
            this.divContainer.text(TrackElementDisplay.getDisplayTrackText(displayable));
        }

        static getDisplayTrackText(displayable:TrackElement):string {
            switch (displayable.datapoint) {
                case "S":
                    return "Gerade";
                case "L":
                    return "Linkskurve";
                case "R":
                    return "Rechtskurve";
            }
        }

        displayTrackImage(displayable:TrackElement) {
            switch (displayable.datapoint) {
                case "S":
                    if(this.currentTrack != this.straightTrack) {
                        if(this.currentTrack) {
                            this.currentTrack.toggleClass('hidden');

                        }
                        this.currentTrack = this.straightTrack;
                    }
                    break;
                case "L":
                    if(this.currentTrack != this.leftTrack) {
                        if(this.currentTrack) {
                            this.currentTrack.toggleClass('hidden');

                        }
                        this.currentTrack = this.leftTrack;
                    }
                    break;
                case "R":
                    if(this.currentTrack != this.rightTrack) {
                        if(this.currentTrack) {
                            this.currentTrack.toggleClass('hidden');
                        }
                        this.currentTrack = this.rightTrack;
                    }
                    break;
            }
            this.currentTrack.removeClass('hidden');

        }
        clear():void {
            this.divContainer.html('');
            this.tracks.addClass('hidden');
        }
        tracks: JQuery = $('.track');
        leftTrack: JQuery = $('.left-track');
        straightTrack: JQuery = $('.straight-track');
        rightTrack: JQuery = $('.right-track');
        currentTrack: JQuery;
        divContainer: JQuery;
        constructor(containerId:String) {
            this.divContainer = $('#' + containerId);
        }

    }
    export class smothieCharDisplay implements Display {
        add = (displayable:Displayable):void  =>{
            this.timeSeries.append(displayable.timestamp
                , displayable.datapoint);
        };

        clear = ():void =>{
            this.timeSeries.clear();
        };

        canvas:SmoothieChart;
        timeSeries: TimeSeries;
        constructor(containerId: string) {
            this.canvas = new SmoothieChart({
                millisPerPixel:49,
                grid:{fillStyle:'#ffffff', strokeStyle:'rgba(192,192,192,0.87)',millisPerLine:10000,verticalSections:4},
                labels:{fillStyle:'#ff0000',fontSize:16,precision:5}});
            this.canvas.streamTo(<HTMLCanvasElement> document.getElementById(containerId));
            this.timeSeries = new TimeSeries();
            this.canvas.addTimeSeries(this.timeSeries, {lineWidth:1.9,strokeStyle:'rgba(40,62,204,1.0)'});

        }
    }
    export class SensorEvent implements Displayable {
        display() {
            this.container.add(this);
        }
        timestamp: Number;
        datapoint: any;
        container: Display;
        constructor(timestamp: Number, value: Number, display: Display) {
            this.timestamp = timestamp;
            this.datapoint = value;
            this.container = display;
        }
    }

    export class VelocityEvent implements Displayable {
        display() {
            this.container.add(this);
        }

        timestamp:Number;
        datapoint:any;
        container:Display;

        constructor(timestamp:Number, value:Number, display:Display) {
            this.timestamp = timestamp;
            this.datapoint = value;
            this.container = display;
        }
    }

    export class RoundTimeEvent implements Displayable {
        datapoint:any;
        timestamp:any;

        parseRoundTime() {
            var roundTimeInSeconds = this.roundTime / 1000;
            return roundTimeInSeconds + ' Sekunden'
        }

        display() {
            this.container.add(this);
        }
        element:JQuery;
        roundTime:number;
        container:Display;

        constructor(roundTime:number, display:Display) {
            this.roundTime = roundTime;
            this.container = display;
            this.element = $('<div class="col-xs-4"><div class="roundtime"></div></div>');
            this.element.find('.roundtime').text('Rundenzeit: ' + this.parseRoundTime());
        }
    }

    export class TrackElement implements Displayable {
        datapoint:any;
        timestamp:any;
        display() {
            this.container.add(this);
        }
        container:Display;

        constructor(trackElement:String, display:Display) {
            this.datapoint = trackElement;
            this.container = display;
        }
    }

    export class Controller {

        gyroZCanvas : smothieCharDisplay = new smothieCharDisplay('gyroZ-canvas');
        velocityCanvas : smothieCharDisplay = new smothieCharDisplay('velocity-canvas');
        trackContainer : TrackElementDisplay = new TrackElementDisplay('current-track-element');
        roundTimesContainer : RoundTimeDisplay = new RoundTimeDisplay('round-times');
        displays: Display[] = [this.gyroZCanvas, this.velocityCanvas, this.trackContainer, this.roundTimesContainer];
        recording: boolean = false;
        recordButton: JQuery;
        clearButton: JQuery;
        constructor(recordbuttonId: string, clearButtonId: string ) {
            this.recordButton = $('#' + recordbuttonId);
            this.clearButton = $('#' + clearButtonId);
            this.recordButton.on('click', this.toggleRecording);
            this.clearButton.on('click', this.clear);
        }
        getDisplayMessage():string {
            if(this.recording) {
                return "Stop Analyse";
            } else {
                return "Starte recording";
            }
        }

        toggleRecording = () => {
            this.recording = !this.recording;
            this.recordButton.text(this.getDisplayMessage());
        };
        clear = () =>  {
            this.displays.forEach(function(dis) {
                dis.clear();
            });
            this.recording = false;
            this.recordButton.text("Starte Analyse")
        };
        parseMessage(message: any) {
            if(this.recording) {
                var type = message.type;
                if(type) {
                    switch (type) {
                        case 'sensorEvent':
                            return new SensorEvent(message.timestamp, message.datapoint, this.gyroZCanvas);
                        case 'velocityEvent':
                            return new VelocityEvent(message.timestamp, message.datapoint, this.velocityCanvas);
                        case 'roundEvent':
                            return new RoundTimeEvent(message.roundTime, this.roundTimesContainer);
                        case 'trackElement':
                            return new TrackElement(message.trackElement, this.trackContainer);
                    }
                }
            }
        }
    }
}






