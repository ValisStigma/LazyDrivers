var db = require('../../persistence/dataAccess');
var express = require('express');
var app = express();

var allowCrossDomain = function(request, response, next) {
    response.header('Access-Control-Allow-Origin', '*');
    response.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    response.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
};

app.use(allowCrossDomain);

function getLatestId(callback) {

    db.SensorEvent
        .findOne({ })
        .sort('-raceId')  // give me the max
        .exec(callback);
}

function getSensorEvents(id, callback) {
    db.SensorEvent.find({raceId:id}).sort('timeStamp').exec(callback);
}
function getVelocityEvents(id, callback) {
    db.VelocityEvent.find({raceId:id}).sort('timeStamp').exec(callback);
}
function getPreviousId(id, callback) {
    db.SensorEvent.findOne({}).where('raceId').gt(0).lt(id).sort('-raceId').exec(callback);
}
function getNextId(id, callback) {
    db.SensorEvent.findOne({}).where('raceId').gt(id).sort('raceId').exec(callback);
}
app.post('/sensorEvents', function(request, response) {
    getSensorEvents(request.body.raceId, function(err, sensorEvents) {
        if(err) {
            response.status(404).send('Couldnt find events');
        } else {
            response.json(sensorEvents);
        }
    })
});

app.post('/velocityEvents', function(request, response) {
    getVelocityEvents(request.body.raceId, function(err, velocityEvents) {
        if(err) {
            response.status(404).send('Couldnt find events');
        } else {
            response.json(velocityEvents);
        }
    })
});

app.post('/previous', function(request, response) {
    getPreviousId(request.body.raceId, function(err, maxEvent) {
        if(err) {
            response.status(404).send('Previous value not found');
        } else {
            response.json(maxEvent.raceId);
        }
    })
});

app.post('/next', function(request, response) {
    getNextId(request.body.raceId, function(err, maxEvent) {
        if(err) {
            response.status(404).send('Next value not found');
        } else {
            response.json(maxEvent.raceId);
        }
    })
});

app.get('/latest', function(request, response) {
    getLatestId(function(err, maxEvent) {
        if(err) {
            response.status(404).send('Max value not found');
        } else {
            response.json(maxEvent.raceId);
        }
    });
});


module.exports = {app:app};