
var db = require('./dataAccess');

function persistTrackElement(elementType) {
    var trackElement = new db.TrackElement({ trackElement: elementType });
    trackElement.save(function (err) {
        if (err) console.log(err);
    });
}

function persistSensorEvent(datapoint, timestamp, raceId) {
    var sensorEvent = new db.SensorEvent({datapoint:datapoint, timestamp: timestamp, raceId: raceId});
    sensorEvent.save(function(err, s) {
        if(err) {
            console.log(err);
        }
    });
}

function persistVelocityEvent(datapoint, timestamp, raceId) {
    var velocityEvent = new db.VelocityEvent({datapoint: datapoint, timestamp: timestamp, raceId: raceId});
    velocityEvent.save(function(err) {
        if(err) console.error(err);
    });
}

function persistRoundEvent(roundtime) {
    var roundEvent = new db.RoundEvent({roundTime:roundtime});
    roundEvent.save(function(err) {
        if(err) console.log(err);
    });
}

function storeMessage(message) {
    var type = message.type;
    if(type) {
        switch (type) {
            case 'sensorEvent':
                persistSensorEvent(message.datapoint, message.timestamp, message.raceId);
                break;
            case 'velocityEvent':
                persistVelocityEvent(message.datapoint, message.timestamp, message.raceId);
                break;
            case 'roundEvent':
                persistRoundEvent(message.roundTime);
                break;
            case 'trackElement':
                persistTrackElement(message.trackElement);
                break;
        }
    }
}

module.exports = {storeMessage: storeMessage};