var histor;
(function (histor) {
    var Controller = (function () {
        function Controller(gyroId, velocityId, nextBtn, prevBtn) {
            var _this = this;
            this.compareEvents = function (a, b) {
                if (a.timestamp < b.timestamp)
                    return -1;
                else if (a.timestamp > b.timestamp)
                    return 1;
                else
                    return 0;
            };
            this.getLatestId = function () {
                var self = _this;
                $.get('/history/latest', function (data) {
                    console.log(data);
                    self.currentId = data;
                    self.getSensorEvents(data);
                    self.getVelocityEvents(data);
                });
            };
            this.getPreviousId = function () {
                _this.resetContainer();
                var x = _this.currentId;
                var self = _this;
                $.post('/history/previous', { raceId: _this.currentId }, function (data) {
                    console.log(data);
                    self.currentId = data;
                    self.getSensorEvents(data);
                    self.getVelocityEvents(data);
                });
            };
            this.getNextId = function () {
                _this.resetContainer();
                var self = _this;
                $.post('/history/next', { raceId: _this.currentId }, function (data) {
                    console.log(data);
                    self.currentId = data;
                    self.getSensorEvents(data);
                    self.getVelocityEvents(data);
                });
            };
            this.chartSensorEvents = function (data, labels, chartId) {
                var lineChartData = {
                    labels: labels,
                    datasets: [
                        {
                            label: "Dataset",
                            fillColor: "#a1EB83",
                            strokeColor: 'rgba(192,192,192,0.87)',
                            pointColor: 'rgba(192,192,192,0.87)',
                            pointStrokeColor: '#27690D',
                            pointHighlightFill: "#fff",
                            pointHighlightStroke: 'rgba(192,192,192,0.87)',
                            data: data
                        }
                    ]
                };
                var canvas = document.getElementById(chartId);
                var ctx = canvas.getContext('2d');
                new Chart(ctx).Line(lineChartData, {
                    responsive: true, scaleShowLabels: false
                });
            };
            this.handleSensorData = function (data) {
                console.log(data);
                var sortedData = _this.getSortedDataPoints(data);
                var labels = _this.getLabels(data);
                _this.chartSensorEvents(sortedData, labels, 'sensor-chart');
            };
            this.handleVelocityData = function (data) {
                console.log(data);
                var sortedData = _this.getSortedDataPoints(data);
                var labels = _this.getLabels(data);
                _this.chartSensorEvents(sortedData, labels, 'velocity-chart');
            };
            this.getSensorEvents = function (id) {
                $.post('/history/sensorEvents', { raceId: id }, _this.handleSensorData);
            };
            this.getVelocityEvents = function (id) {
                $.post('/history/velocityEvents', { raceId: id }, _this.handleVelocityData);
            };
            this.getSortedDataPoints = function (data) {
                var ret = [];
                data.sort(_this.compareEvents);
                data.forEach(function (elem) {
                    ret.push(elem.datapoint);
                });
                return ret;
            };
            this.getLabels = function (data) {
                var ret = [];
                data.forEach(function (elem) {
                    ret.push("");
                });
                return ret;
            };
            this.resetContainer = function () {
                _this.gyroContainer.html('<canvas id="sensor-chart" height="600" width="2000"></canvas>');
                _this.velocityContainer.html('<canvas id="velocity-chart" height="600" width="2000"></canvas>');
            };
            this.gyroContainer = $('#' + gyroId);
            this.velocityContainer = $('#' + velocityId);
            this.nextButton = $('#' + nextBtn);
            this.prevButton = $('#' + prevBtn);
            this.nextButton.on('click', this.getNextId);
            this.prevButton.on('click', this.getPreviousId);
            this.getLatestId();
        }
        return Controller;
    }());
    histor.Controller = Controller;
})(histor || (histor = {}));
//# sourceMappingURL=history.js.map