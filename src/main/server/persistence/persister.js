
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var trackElementSchema = new Schema({
    trackElement: String
    //_id : Schema.ObjectId
});

var sensorEventSchema = new Schema({
    datapoint: Number,
    timestamp: Number
    //_id : Schema.ObjectId
});

var velocityEventSchema = new Schema({
    datapoint: Number,
    timestamp: Number
    //_id : Schema.ObjectId
});
var roundEventSchema = new Schema({
    roundTime: Number
    //_id : Schema.ObjectId
});

var TrackElement = mongoose.model('TrackElement', trackElementSchema, "trackelements");
var SensorEvent = mongoose.model('SensorEvent', sensorEventSchema, "sensorevents");
var VelocityEvent = mongoose.model('VelocityEvent', velocityEventSchema, "velocityevents");
var RoundEvent = mongoose.model('RoundEvent', roundEventSchema, "roundevents");



mongoose.connect('mongodb://localhost/LazyDrivers');

function persistTrackElement(elementType) {
    var trackElement = new TrackElement({ trackElement: elementType });
    trackElement.save(function (err) {
        if (err) console.log(err);
    });
}

function persistSensorEvent(datapoint, timestamp) {
    var sensorEvent = new SensorEvent({datapoint:datapoint, timestamp: timestamp});
    sensorEvent.save(function(err, s) {
        if(err) {
            console.log(err);
        } else {
            console.log(s);

        }
    });
}

function persistVelocityEvent(datapoint, timestamp) {
    var velocityEvent = new VelocityEvent({datapoint: datapoint, timestamp: timestamp});
    velocityEvent.save(function(err) {
        if(err) console.error(err);
    });
}

function persistRoundEvent(roundtime) {
    var roundEvent = new RoundEvent({roundTime:roundtime});
    roundEvent.save(function(err) {
        if(err) console.log(err);
    });
}

function storeMessage(message) {
    var type = message.type;
    if(type) {
        switch (type) {
            case 'sensorEvent':
                persistSensorEvent(message.datapoint, message.timestamp);
                break;
            case 'velocityEvent':
                persistVelocityEvent(message.datapoint, message.timestamp);
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