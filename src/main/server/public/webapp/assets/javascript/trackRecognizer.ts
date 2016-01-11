
module trackRecognizer {
    import TrackElement = MyModule.TrackElement;
    import RoundTimeEvent = MyModule.RoundTimeEvent;
    export class TrackRecognizer {
        trackElements: TrackElement[] =[];
        trackSequence: TrackElement[] = [];
        dirty:boolean = true;
        container: JQuery;
        trackString: string ="";
        index: number = 0;
        latestTimeStamp: number = 0;
        constructor(containerId: string) {
            this.container = $('#' + containerId);
        }

        recordTrack(trackElement: TrackElement) {

            if(this.trackElements.length > 0 && this.trackElements[this.trackElements.length - 1].datapoint != trackElement.datapoint) {
                this.index++;
                if(!this.dirty) {
                    this.paintTrack();
                }

            }
            if(this.trackElements.length == 0 || this.trackElements[this.trackElements.length - 1].datapoint != trackElement.datapoint) {
                if(this.latestTimeStamp <= trackElement.timestamp) {
                    this.trackElements.push(trackElement);

                }

            }

        }
        paintTrack() {
            var repr: string = this.trackString +"<br>" + this.buildIndexString();
            this.container.html(repr);
        }

        buildIndexString() {
            var indexString: string = "=";
            for(var i = 0; i < this.index; i++) {
                indexString += "====";
            }
            indexString += "O";
            return indexString;
        }
        buildTrack() {
            this.trackString = "";
            for(var i = 0; i < this.trackSequence.length; i++) {
                this.trackString += "=" + this.trackSequence[i].datapoint + "=|";
            }

            this.container.text(this.trackString);

        }

        analyzeTrack(roundElement: RoundTimeEvent) {
            var temp = this.trackElements;
            this.trackElements = [];
            this.index = 0;
            var oldRoundTime = this.latestTimeStamp;
            this.latestTimeStamp = roundElement.timestamp;
            var analyzedTrack = temp.filter(function(item, pos, arr){
                return (pos === 0 || item.datapoint !== arr[pos-1].datapoint) && item.timestamp >= oldRoundTime;
            });
            if(!trackRecognizer.TrackRecognizer.tracksAreEqual(this.trackSequence, analyzedTrack)) {
                this.trackSequence = analyzedTrack;
                console.log("changed");
                this.dirty = true;
                this.buildTrack();
            } else {
                this.dirty = false;
            }

            this.printTrack(this.trackSequence);
        }

        printTrack(trackelements: TrackElement[]) {
            var track: string = " ";
            trackelements.forEach(function(trackElement) {
                track += trackElement.datapoint;
            });
            console.log(track);
        }

        static tracksAreEqual (track1: TrackElement[], track2: TrackElement[]) {
        if (track2.length != track1.length)
            return false;
        for (var i = 0, l=track2.length; i < l; i++) {
            if (track2[i].datapoint != track1[i].datapoint) {
                return false;
            }
        }
        return true;
    }

    }
}