"use strict";
var smoothie_1 = require("smoothie");
var smoothie_2 = require("smoothie");
var jquery_1 = require("jquery");
jquery_1.$(function () {
    var gyroZCanvas = new smoothie_1.SmoothieChart({ millisPerPixel: 49, grid: { fillStyle: '#ffffff',
            strokeStyle: 'rgba(192,192,192,0.87)', millisPerLine: 10000, verticalSections: 4 }, labels: { fillStyle: '#ff0000', fontSize: 16, precision: 5 } });
    gyroZCanvas.streamTo(document.getElementById("gyroZ-canvas"));
    var gyroZ = new smoothie_2.TimeSeries();
    gyroZCanvas.addTimeSeries(gyroZ, { lineWidth: 1.9, strokeStyle: '#1621e9' });
    var velocityCanvas = new smoothie_1.SmoothieChart({ millisPerPixel: 49, grid: { fillStyle: '#ffffff',
            strokeStyle: 'rgba(192,192,192,0.87)', millisPerLine: 10000, verticalSections: 4 }, labels: { fillStyle: '#ff0000', fontSize: 16, precision: 5 } });
    velocityCanvas.streamTo(document.getElementById("velocity-canvas"));
    var velocity = new smoothie_2.TimeSeries();
    velocityCanvas.addTimeSeries(velocity, { lineWidth: 1.9, strokeStyle: '#1621e9' });
    var socket = io.connect('http://localhost:8070');
    var roundTimesDiv = jquery_1.$('#round-times');
    var currentTrackElement = jquery_1.$('#current-track-element');
    socket.on('pushdata', function (data) {
        data.forEach(function (message) {
            var type = message.type;
            if (type) {
                console.log(type);
                switch (type) {
                    case 'sensorEvent':
                        gyroZ.append(message.timestamp, message.datapoint);
                        break;
                    case 'velocityEvent':
                        velocity.append(message.timestamp, message.datapoint);
                        break;
                    case 'roundEvent':
                        console.log(message);
                        var temp = jquery_1.$('<div class="col-xs-4"><div class="roundtime"></div></div>');
                        temp.find('.roundtime').text('Rundenzeit: ' + parseRoundTime(message.roundTime));
                        roundTimesDiv.append(temp);
                        break;
                    case 'trackElement':
                        console.log(message);
                        currentTrackElement.text(message.trackElement);
                }
            }
        });
    });
});
var SensorEvent = (function () {
    function SensorEvent(timestamp, value, display) {
        this.timestamp = timestamp;
        this.value = value;
        this.container = display;
    }
    SensorEvent.prototype.display = function () {
        this.container.add(this);
    };
    return SensorEvent;
}());
var VelocityEvent = (function () {
    function VelocityEvent(timestamp, value, display) {
        this.timestamp = timestamp;
        this.value = value;
        this.container = display;
    }
    VelocityEvent.prototype.display = function () {
        this.container.add(this);
    };
    return VelocityEvent;
}());
var RoundTimeEvent = (function () {
    function RoundTimeEvent(roundTime, display) {
        this.roundTime = roundTime;
        this.container = display;
    }
    RoundTimeEvent.prototype.parseRoundTime = function () {
        var milisceconds = parseFloat(this.roundTime);
        var roundTimeInSeconds = milisceconds / 1000;
        return roundTimeInSeconds + ' Sekunden';
    };
    RoundTimeEvent.prototype.display = function () {
        var temp = jquery_1.$('<div class="col-xs-4"><div class="roundtime"></div></div>');
        temp.find('.roundtime').text('Rundenzeit: ' + this.parseRoundTime());
        this.container.add(temp);
    };
    return RoundTimeEvent;
}());
var TrackElement = (function () {
    function TrackElement(trackElement, display) {
        this.trackElement = trackElement;
        this.container = display;
    }
    TrackElement.prototype.display = function () {
        this.container.add(this.trackElement);
    };
    return TrackElement;
}());
//# sourceMappingURL=main.js.map