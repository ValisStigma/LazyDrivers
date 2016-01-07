/**
 * Created by rafael on 05.11.2015.
 */
function parseRoundTime(roundTime) {
    var milisceconds = parseFloat(roundTime);
    var roundTimeInSeconds = milisceconds / 1000;
    return roundTimeInSeconds + ' Sekunden'
}

$(function() {

    var gyroZCanvas = new SmoothieChart({millisPerPixel:49,grid:{fillStyle:'#ffffff',
        strokeStyle:'rgba(192,192,192,0.87)',millisPerLine:10000,verticalSections:4},labels:{fillStyle:'#ff0000',fontSize:16,precision:5}});
    gyroZCanvas.streamTo(document.getElementById("gyroZ-canvas"));
    var gyroZ = new TimeSeries();
    gyroZCanvas.addTimeSeries(gyroZ, {lineWidth:1.9,strokeStyle:'#1621e9'});



    var velocityCanvas = new SmoothieChart({millisPerPixel:49,grid:{fillStyle:'#ffffff',
        strokeStyle:'rgba(192,192,192,0.87)',millisPerLine:10000,verticalSections:4},labels:{fillStyle:'#ff0000',fontSize:16,precision:5}});
    velocityCanvas.streamTo(document.getElementById("velocity-canvas"));
    var velocity = new TimeSeries();
    velocityCanvas.addTimeSeries(velocity, {lineWidth:1.9,strokeStyle:'#1621e9'});

    var socket = io.connect('http://localhost:8070');
    var roundTimesDiv = $('#round-times');
    var currentTrackElement = $('#current-track-element');


    socket.on('pushdata', function (data) {
        data.forEach(function(message) {
            var type = message.type;
            if(type) {
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
                        var temp = $('<div class="col-xs-4"><div class="roundtime"></div></div>');
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