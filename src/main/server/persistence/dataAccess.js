
var mongoose = require('mongoose');
var Schema = mongoose.Schema;
//mongoose.connect('mongodb://localhost/LazyDrivers');
mongoose.createConnection('mongodb://localhost/LazyDrivers');
var trackElementSchema = new Schema({
    trackElement: String
});
mongoose.connection.on('error', function (err) {
    // Do something
    var x = 2;
});


var sensorEventSchema = new Schema({
    datapoint: Number,
    timestamp: Number,
    raceId: Number

});

var velocityEventSchema = new Schema({
    datapoint: Number,
    timestamp: Number,
    raceId: Number
});
var roundEventSchema = new Schema({
    roundTime: Number
});

var TrackElement = mongoose.model('TrackElement', trackElementSchema, "trackelements");
var SensorEvent = mongoose.model('SensorEvent', sensorEventSchema, "sensorevents");
var VelocityEvent = mongoose.model('VelocityEvent', velocityEventSchema, "velocityevents");
var RoundEvent = mongoose.model('RoundEvent', roundEventSchema, "roundevents");


module.exports = {TrackElement: TrackElement, SensorEvent: SensorEvent, VelocityEvent: VelocityEvent, RoundEvent: RoundEvent};