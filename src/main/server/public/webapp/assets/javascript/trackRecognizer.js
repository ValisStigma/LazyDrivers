var trackRecognizer;
(function (trackRecognizer) {
    var TrackRecognizer = (function () {
        function TrackRecognizer(containerId) {
            this.trackElements = [];
            this.trackSequence = [];
            this.dirty = true;
            this.trackString = "";
            this.index = 0;
            this.latestTimeStamp = 0;
            this.container = $('#' + containerId);
        }
        TrackRecognizer.prototype.recordTrack = function (trackElement) {
            if (this.trackElements.length > 0 && this.trackElements[this.trackElements.length - 1].datapoint != trackElement.datapoint) {
                this.index++;
                if (!this.dirty) {
                    this.paintTrack();
                }
            }
            if (this.trackElements.length == 0 || this.trackElements[this.trackElements.length - 1].datapoint != trackElement.datapoint) {
                if (this.latestTimeStamp <= trackElement.timestamp) {
                    this.trackElements.push(trackElement);
                }
            }
        };
        TrackRecognizer.prototype.paintTrack = function () {
            var repr = this.trackString + "<br>" + this.buildIndexString();
            this.container.html(repr);
        };
        TrackRecognizer.prototype.buildIndexString = function () {
            var indexString = "=";
            for (var i = 0; i < this.index; i++) {
                indexString += "====";
            }
            indexString += "O";
            return indexString;
        };
        TrackRecognizer.prototype.buildTrack = function () {
            this.trackString = "";
            for (var i = 0; i < this.trackSequence.length; i++) {
                this.trackString += "=" + this.trackSequence[i].datapoint + "=|";
            }
            this.container.text(this.trackString);
        };
        TrackRecognizer.prototype.analyzeTrack = function (roundElement) {
            var temp = this.trackElements;
            this.trackElements = [];
            this.index = 0;
            var oldRoundTime = this.latestTimeStamp;
            this.latestTimeStamp = roundElement.timestamp;
            var analyzedTrack = temp.filter(function (item, pos, arr) {
                return (pos === 0 || item.datapoint !== arr[pos - 1].datapoint) && item.timestamp >= oldRoundTime;
            });
            if (!trackRecognizer.TrackRecognizer.tracksAreEqual(this.trackSequence, analyzedTrack)) {
                this.trackSequence = analyzedTrack;
                console.log("changed");
                this.dirty = true;
                this.buildTrack();
            }
            else {
                this.dirty = false;
            }
            this.printTrack(this.trackSequence);
        };
        TrackRecognizer.prototype.printTrack = function (trackelements) {
            var track = " ";
            trackelements.forEach(function (trackElement) {
                track += trackElement.datapoint;
            });
            console.log(track);
        };
        TrackRecognizer.tracksAreEqual = function (track1, track2) {
            if (track2.length != track1.length)
                return false;
            for (var i = 0, l = track2.length; i < l; i++) {
                if (track2[i].datapoint != track1[i].datapoint) {
                    return false;
                }
            }
            return true;
        };
        return TrackRecognizer;
    }());
    trackRecognizer.TrackRecognizer = TrackRecognizer;
})(trackRecognizer || (trackRecognizer = {}));
//# sourceMappingURL=trackRecognizer.js.map