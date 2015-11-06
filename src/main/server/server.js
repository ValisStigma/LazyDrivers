var express = require('express');
var bodyParser = require('body-parser');

var allowCrossDomain = function(request, response, next) {
    response.header('Access-Control-Allow-Origin', '*');
    response.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    response.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
};

var dataReciever = require('./serverRoutes/dataReciever/dataReciever');



var app = express();
app.use(allowCrossDomain);
app.use(bodyParser.json());
app.use('/', express.static(__dirname + '/public/webapp'));
app.use('/api', dataReciever.app);

var	server = require('http').createServer(app);
var io = require('socket.io').listen(server);



io.sockets.on('connection', function (socket) {

    setInterval(function(){
        var data = dataReciever.getDataPoints();
        io.sockets.emit('pushdata', data);
    },2000);

});

var appPort = 8070;
server.listen(appPort);
console.log('Server is listening on port 8070...');