jQuery(document).ready(function ($) {
    (function worker() {
        $.ajax({
            url: '/ajax/current',
            dataType: 'json',
            success: function (data) {
                //console.log(data); 
                var html = '<table><tr>';
                html += '<th>Server</th><th>Duration</th><th>Received</th><th>Request Id</th><th>Status</th><th>Info</th>';
                html += '</tr>';
                jQuery.each(data, function (i, val) {
                    html += '<tr><td class="col1">' + val.securityServer + '</td>';
                    html += '<td class="col2">' + val.duration + ' ms</td>';
                    html += '<td class="col3">' + (val.end === null ? '-' : val.end) + '</td>';
                    html += '<td class="col4">' + val.requestId + '</td>';
                    html += '<td class="col5"><span class="label ' + (val.status ? 'success' : 'failure') + '">' + (val.status ? 'OK' : 'NOK') + '</span></td>';
                    html += '<td class="col6">' + val.faultCode + '</td></tr>';
                });
                html += '</tr></table>';
                $("div#container").html(html);
                var now = new Date();
                $("div#updated").html("Last update: " + now.toLocaleDateString("fi-FI") + " " + now.getHours() + ":" + now.getMinutes() + ":" + now.getSeconds());
            },
            error: function (data, status, error) {
                console.log(data);
                console.log(status);
                console.log(error);
            },
            complete: function () {
                // Schedule the next request when the current one's complete
                setTimeout(worker, 30000);
            }
        });
    })();
});