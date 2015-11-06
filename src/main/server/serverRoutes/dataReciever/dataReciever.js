var express = require('express');
var data = [];
var app = express();
var allowCrossDomain = function(request, response, next) {
    response.header('Access-Control-Allow-Origin', '*');
    response.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    response.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
};

app.use(allowCrossDomain);


function getDataPoints() {

    var exportData = data.slice();
    data = [];
    return exportData;
}


app.post('/', function(request, response) {
    var point = request.body.data;
    console.log(point);
    data.push(point);
    response.json(point);
});


module.exports = {app:app, getDataPoints: getDataPoints};