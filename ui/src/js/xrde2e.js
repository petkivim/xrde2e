jQuery(document).ready(function ($) {
    (function worker() {
        $.ajax({
            url: '/ajax/current',
            dataType: 'json',
            success: function (data) {
                //console.log(data); 
                var html = '<table><tr>';
                html += '<th>Server</th><th>Duration</th><th>Created</th><th>Request Id</th><th>Status</th><th>Info</th>';
                html += '</tr>';
                jQuery.each(data, function (i, val) {
                    var tooltipCol1 = '<span data-tooltip="' + val.securityServer + '" data-tooltip-position="right">' + (val.label.length > 0 ? val.label : val.securityServer) + '</span>';
                    var tooltipCol2 = '<span data-tooltip="Sent: ' + val.begin + ' Received:' + val.end + '" data-tooltip-position="left">' + val.duration + '</span>';
                    html += '<tr><td class="col1">' + tooltipCol1 + '</td>';
                    html += '<td class="col2">' + tooltipCol2 + ' ms</td>';
                    html += '<td class="col3">' + val.createdDate + '</td>';
                    html += '<td class="col4">' + val.requestId + '</td>';
                    html += '<td class="col5"><span class="label ' + (val.status ? 'success' : 'failure') + '">' + (val.status ? 'OK' : 'NOK') + '</span></td>';
                    html += '<td class="col6">' + val.faultCode + '<br /> ' + val.faultString + '</td></tr>';
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
                setTimeout(worker, 5000);
            }
        });
    })();
});