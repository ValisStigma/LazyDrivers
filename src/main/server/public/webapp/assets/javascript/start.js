var socket = io.connect('http://localhost:8070');
var disp = new MyModule.Controller('record-btn', 'reload-btn');
socket.on('pushdata', function (data) {
    data.forEach(function(message) {
        var el = disp.parseMessage(message);
        if(el) {
            el.display();

        }

    });
});