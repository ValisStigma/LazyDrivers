define("main", ["require", "exports", "smoothie", "smoothie"], function (require, exports, smoothie_1, smoothie_2) {
    "use strict";
    var RoundTimeDisplay = (function () {
        function RoundTimeDisplay(containerId) {
            this.divContainer = $('#' + containerId);
        }
        RoundTimeDisplay.prototype.add = function (displayable) {
            this.divContainer.append(displayable.element);
        };
        RoundTimeDisplay.prototype.clear = function () {
            this.divContainer.html('');
        };
        return RoundTimeDisplay;
    }());
    var TrackElementDisplay = (function () {
        function TrackElementDisplay(containerId) {
            this.divContainer = $('#' + containerId);
        }
        TrackElementDisplay.prototype.add = function (displayable) {
            this.divContainer.text(displayable.datapoint);
        };
        TrackElementDisplay.prototype.clear = function () {
            this.divContainer.html('');
        };
        return TrackElementDisplay;
    }());
    var smothieCharDisplay = (function () {
        function smothieCharDisplay(containerId) {
            this.canvas = new smoothie_1.SmoothieChart({
                millisPerPixel: 49,
                grid: { fillStyle: '#ffffff', stokeStyle: 'rgba(192,192,192,0.87)', millisPerLine: 10000, verticalSections: 4 },
                labels: { fillStyle: '#ff0000', fontSize: 16, precision: 5 } });
            this.canvas.streamTo(document.getElementById(containerId));
            this.timeSeries = new smoothie_2.TimeSeries();
            this.canvas.addTimeSeries(this.timeSeries, { lineWidth: 1.9, stokeStyle: '#1621e9' });
        }
        smothieCharDisplay.prototype.add = function (displayable) {
            this.timeSeries.append(displayable.datapoint, displayable.datapoint);
        };
        smothieCharDisplay.prototype.clear = function () {
            this.timeSeries.clear();
        };
        return smothieCharDisplay;
    }());
    var SensorEvent = (function () {
        function SensorEvent(timestamp, value, display) {
            this.timestamp = timestamp;
            this.datapoint = value;
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
            this.datapoint = value;
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
            this.element = $('<div class="col-xs-4"><div class="roundtime"></div></div>');
            this.element.find('.roundtime').text('Rundenzeit: ' + this.parseRoundTime());
        }
        RoundTimeEvent.prototype.parseRoundTime = function () {
            var roundTimeInSeconds = this.roundTime / 1000;
            return roundTimeInSeconds + ' Sekunden';
        };
        RoundTimeEvent.prototype.display = function () {
            this.container.add(this);
        };
        return RoundTimeEvent;
    }());
    var TrackElement = (function () {
        function TrackElement(trackElement, display) {
            this.datapoint = trackElement;
            this.container = display;
        }
        TrackElement.prototype.display = function () {
            this.container.add(this);
        };
        return TrackElement;
    }());
    var Controller = (function () {
        function Controller() {
            this.gyroZCanvas = new smothieCharDisplay('gyroZ-canvas');
            this.velocityCanvas = new smothieCharDisplay('velocity-canvas');
            this.trackContainer = new TrackElementDisplay('#current-track-element');
            this.roundTimesContainer = new RoundTimeDisplay('#round-times');
            this.displays = [this.gyroZCanvas, this.velocityCanvas, this.trackContainer, this.roundTimesContainer];
            this.recording = false;
        }
        Controller.prototype.getDisplayMessage = function () {
            if (this.recording) {
                return "Stop recording";
            }
            else {
                return "Start recording";
            }
        };
        Controller.prototype.toggleRecording = function () {
            this.recording = !this.recording;
        };
        Controller.prototype.clear = function () {
            this.displays.forEach(function (dis) {
                dis.clear();
            });
        };
        Controller.prototype.parseMessage = function (message) {
            if (this.recording) {
                var type = message.type;
                if (type) {
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
        };
        return Controller;
    }());
    $(function () {
        var socket = io.connect('http://localhost:8070');
        var disp = new Controller();
        socket.on('pushdata', function (data) {
            data.forEach(function (message) {
                var el = disp.parseMessage(message);
                el.display();
            });
        });
    });
});
//# sourceMappingURL=main.js.map