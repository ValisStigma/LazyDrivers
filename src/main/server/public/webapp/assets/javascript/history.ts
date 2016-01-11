
module histor {




    export class Controller {
        velocityContainer:JQuery;
        gyroContainer:JQuery;
        nextButton: JQuery;
        prevButton: JQuery;
        currentId:string;
        compareEvents = (a: any,b: any) => {
            if (a.timestamp < b.timestamp)
                return -1;
            else if (a.timestamp > b.timestamp)
                return 1;
            else
                return 0;
        };


        getLatestId = () => {
            var self = this;
            $.get('/history/latest', function(data) {
                console.log(data);
                self.currentId = data;
                self.getSensorEvents(data);
                self.getVelocityEvents(data);
            });
        };
        getPreviousId = () => {
            this.resetContainer();
            var x = this.currentId;
            var self = this;
            $.post('/history/previous', {raceId: this.currentId}, function(data) {
                console.log(data);
                self.currentId = data;
                self.getSensorEvents(data);
                self.getVelocityEvents(data);
            });
        };

        getNextId = () => {
            this.resetContainer();
            var self = this;
            $.post('/history/next', {raceId: this.currentId}, function(data) {
                console.log(data);
                self.currentId = data;
                self.getSensorEvents(data);
                self.getVelocityEvents(data);
            });
        };
        chartSensorEvents =(data, labels, chartId)=> {
            var lineChartData: LinearChartData = {
                labels: labels,
                datasets : [
                    {
                        label: "Dataset",
                        fillColor : "#a1EB83",
                        strokeColor : 'rgba(192,192,192,0.87)',
                        pointColor : 'rgba(192,192,192,0.87)',
                        pointStrokeColor : '#27690D',
                        pointHighlightFill : "#fff",
                        pointHighlightStroke : 'rgba(192,192,192,0.87)',
                        data : data
                    }
                ]
            };

            var canvas = <HTMLCanvasElement>document.getElementById(chartId);
            var ctx = canvas.getContext('2d');
            new Chart(ctx).Line(lineChartData, {
                responsive: true,scaleShowLabels: false
            });


        };
        handleSensorData = (data: any[]) => {
            console.log(data);
            var sortedData = this.getSortedDataPoints(data);
            var labels = this.getLabels(data);
            this.chartSensorEvents(sortedData, labels, 'sensor-chart');
        };
        handleVelocityData = (data:any[]) => {
            console.log(data);
            var sortedData = this.getSortedDataPoints(data);
            var labels = this.getLabels(data);
            this.chartSensorEvents(sortedData, labels, 'velocity-chart');

        };
        public getSensorEvents = (id: string) => {
            $.post('/history/sensorEvents', {raceId:id},  this.handleSensorData);
        };

        public getVelocityEvents = (id: string) => {
            $.post('/history/velocityEvents', {raceId:id},  this.handleVelocityData);
        };



        getSortedDataPoints = (data: any[]) => {
            var ret: any[] = [];

            data.sort(this.compareEvents);
            data.forEach(function(elem) {
                ret.push(elem.datapoint);
            });
            return ret;
        };

        getLabels = (data: any[]) => {
            var ret: any[] = [];

            data.forEach(function(elem) {
                ret.push("");
            });
            return ret;
        };

        resetContainer = () => {
            this.gyroContainer.html('<canvas id="sensor-chart" height="600" width="2000"></canvas>');
            this.velocityContainer.html('<canvas id="velocity-chart" height="600" width="2000"></canvas>');
        }

        constructor(gyroId: string, velocityId: string, nextBtn: string, prevBtn: string) {
            this.gyroContainer = $('#'+ gyroId);
            this.velocityContainer = $('#' + velocityId);
            this.nextButton = $('#' + nextBtn);
            this.prevButton = $('#' + prevBtn);
            this.nextButton.on('click', this.getNextId);
            this.prevButton.on('click', this.getPreviousId);
            this.getLatestId();
        }
    }
}