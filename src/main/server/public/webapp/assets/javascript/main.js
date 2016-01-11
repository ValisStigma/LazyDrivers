/// <reference path="Typefiles/smoothie.d.ts"/>
/// <reference path="Typefiles/jquery.d.ts"/>
var MyModule;
(function (MyModule) {
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
    MyModule.RoundTimeDisplay = RoundTimeDisplay;
    var TrackElementDisplay = (function () {
        function TrackElementDisplay(containerId) {
            this.tracks = $('.track');
            this.leftTrack = $('.left-track');
            this.straightTrack = $('.straight-track');
            this.rightTrack = $('.right-track');
            this.divContainer = $('#' + containerId);
        }
        TrackElementDisplay.prototype.add = function (displayable) {
            this.displayTrackImage(displayable);
            this.divContainer.text(TrackElementDisplay.getDisplayTrackText(displayable));
        };
        TrackElementDisplay.getDisplayTrackText = function (displayable) {
            switch (displayable.datapoint) {
                case "S":
                    return "Gerade";
                case "L":
                    return "Linkskurve";
                case "R":
                    return "Rechtskurve";
            }
        };
        TrackElementDisplay.prototype.displayTrackImage = function (displayable) {
            switch (displayable.datapoint) {
                case "S":
                    if (this.currentTrack != this.straightTrack) {
                        if (this.currentTrack) {
                            this.currentTrack.toggleClass('hidden');
                        }
                        this.currentTrack = this.straightTrack;
                    }
                    break;
                case "L":
                    if (this.currentTrack != this.leftTrack) {
                        if (this.currentTrack) {
                            this.currentTrack.toggleClass('hidden');
                        }
                        this.currentTrack = this.leftTrack;
                    }
                    break;
                case "R":
                    if (this.currentTrack != this.rightTrack) {
                        if (this.currentTrack) {
                            this.currentTrack.toggleClass('hidden');
                        }
                        this.currentTrack = this.rightTrack;
                    }
                    break;
            }
            this.currentTrack.removeClass('hidden');
        };
        TrackElementDisplay.prototype.clear = function () {
            this.divContainer.html('');
            this.tracks.addClass('hidden');
        };
        return TrackElementDisplay;
    }());
    MyModule.TrackElementDisplay = TrackElementDisplay;
    var smothieCharDisplay = (function () {
        function smothieCharDisplay(containerId) {
            var _this = this;
            this.add = function (displayable) {
                _this.timeSeries.append(displayable.timestamp, displayable.datapoint);
            };
            this.clear = function () {
                _this.timeSeries.clear();
            };
            this.canvas = new SmoothieChart({
                millisPerPixel: 49,
                grid: { fillStyle: '#ffffff', strokeStyle: 'rgba(192,192,192,0.87)', millisPerLine: 10000, verticalSections: 4 },
                labels: { fillStyle: '#ff0000', fontSize: 16, precision: 5 } });
            this.canvas.streamTo(document.getElementById(containerId));
            this.timeSeries = new TimeSeries();
            this.canvas.addTimeSeries(this.timeSeries, { lineWidth: 1.9, strokeStyle: 'rgba(40,62,204,1.0)' });
        }
        return smothieCharDisplay;
    }());
    MyModule.smothieCharDisplay = smothieCharDisplay;
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
    MyModule.SensorEvent = SensorEvent;
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
    MyModule.VelocityEvent = VelocityEvent;
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
    MyModule.RoundTimeEvent = RoundTimeEvent;
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
    MyModule.TrackElement = TrackElement;
    var Controller = (function () {
        function Controller(recordbuttonId, clearButtonId) {
            var _this = this;
            this.gyroZCanvas = new smothieCharDisplay('gyroZ-canvas');
            this.velocityCanvas = new smothieCharDisplay('velocity-canvas');
            this.trackContainer = new TrackElementDisplay('current-track-element');
            this.roundTimesContainer = new RoundTimeDisplay('round-times');
            this.displays = [this.gyroZCanvas, this.velocityCanvas, this.trackContainer, this.roundTimesContainer];
            this.recording = false;
            this.toggleRecording = function () {
                _this.recording = !_this.recording;
                _this.recordButton.text(_this.getDisplayMessage());
            };
            this.clear = function () {
                _this.displays.forEach(function (dis) {
                    dis.clear();
                });
                _this.recording = false;
                _this.recordButton.text("Starte Analyse");
            };
            this.recordButton = $('#' + recordbuttonId);
            this.clearButton = $('#' + clearButtonId);
            this.recordButton.on('click', this.toggleRecording);
            this.clearButton.on('click', this.clear);
        }
        Controller.prototype.getDisplayMessage = function () {
            if (this.recording) {
                return "Stop Analyse";
            }
            else {
                return "Starte recording";
            }
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
    MyModule.Controller = Controller;
})(MyModule || (MyModule = {}));
//# sourceMappingURL=main.js.map