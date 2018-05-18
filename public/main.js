var logs = [];
$(document).ready(function(){
    $("#download").click(function() {
        const link = $('input[name="link"]').val();
        ws.send(link);
        $('#logs').show();
    });
    const url = $('body')[0].attributes.url.value;
    var ws = new WebSocket("ws://" +  url);

    ws.onmessage = function (ev) {
        const msg = ev.data;
        if (msg.startsWith("Downloaded:")) {
            $('form input[name="file"]').val(msg.split("Downloaded:")[1]);
            document.getElementsByTagName('form')[0].submit();
        } else {
            if (logs.length >= 15) logs.pop();
            logs.push(msg);
            const ps = logs.map(log => {
                if (log.startsWith("Unable")) return '<p style="color: #dd3313;">' + log + '</p>';
                else return "<p>" + log + "</p>"
            });
            $('#logs').empty().append(ps);
        }
    };

    ws.onclose = function (ev) {
        $('.ui.basic.modal').modal('show');
    };

    ws.onerror = function (ev) {
        console.log("Opps! Error:");
        console.log(ev);
    };
});
