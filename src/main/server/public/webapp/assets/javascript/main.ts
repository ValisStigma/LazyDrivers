


import {SmoothieChart} from "smoothie";
import {TimeSeries} from "smoothie";

interface Displayable {
    display():void;
    datapoint:any;
    timestamp:any;
}

interface Display {
    add(displayable: Displayable):void;
    clear(): void;
}

class RoundTimeDisplay implements Display {
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
class TrackElementDisplay implements Display {
    add(displayable:TrackElement):void {
        this.divContainer.text(displayable.datapoint);
    }

    clear():void {
        this.divContainer.html('');
    }
    divContainer: JQuery;
    constructor(containerId:String) {
        this.divContainer = $('#' + containerId);
    }

}
class smothieCharDisplay implements Display {
    add(displayable:Displayable):void {
        this.timeSeries.append(displayable.datapoint
            , displayable.datapoint);
    }

    clear():void {
        this.timeSeries.clear();
    }

    canvas:SmoothieChart;
    timeSeries: TimeSeries;
    constructor(containerId: string) {
        this.canvas = new SmoothieChart({
            millisPerPixel:49,
            grid:{fillStyle:'#ffffff', stokeStyle:'rgba(192,192,192,0.87)',millisPerLine:10000,verticalSections:4},
            labels:{fillStyle:'#ff0000',fontSize:16,precision:5}});
        this.canvas.streamTo(<HTMLCanvasElement> document.getElementById(containerId));
        this.timeSeries = new TimeSeries();
        this.canvas.addTimeSeries(this.timeSeries, {lineWidth:1.9,stokeStyle:'#1621e9'});

    }
}
class SensorEvent implements Displayable {
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

class VelocityEvent implements Displayable {
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

class RoundTimeEvent implements Displayable {
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

class TrackElement implements Displayable {
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

class Controller {

    gyroZCanvas : smothieCharDisplay = new smothieCharDisplay('gyroZ-canvas');
    velocityCanvas : smothieCharDisplay = new smothieCharDisplay('velocity-canvas');
    trackContainer : TrackElementDisplay = new TrackElementDisplay('#current-track-element');
    roundTimesContainer : RoundTimeDisplay = new RoundTimeDisplay('#round-times');
    constructor() {

    }

    parseMessage(message: any) {
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

$(function() {

    var socket = io.connect('http://localhost:8070');
    var disp = new Controller();
    socket.on('pushdata', function (data: any) {
        data.forEach(function(message: any) {
            var el = disp.parseMessage(message);
            el.display();

        });
    });

});


