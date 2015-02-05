/*
 * Copyright 2014 SeaClouds
 * Contact: SeaClouds
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
var SPINNER = new Spinner({lines: 13, length: 6, width: 2, radius: 5, top: "-5px"}).spin(document.getElementById("loading-spinner"));
var APP_ID = location.search.split('id=')[1];
var CONTENT_ID = "page-content";
var ACTIVE_API_CALLS = [];
var DATATABLE;


$(document).ready(function () {

    PNotify.prototype.options.styling = "fontawesome";

    setupDataTable();
    showAvailableMetrics();
});


function setupDataTable() {
    $("#" + "metric-table-caption").html("<strong>Available metrics for the application (" + APP_ID + ")</strong>");
    DATATABLE = $('#metrics-table').DataTable({
        responsive: true,
        "lengthMenu": [
            [20, 50, -1],
            [20, 50, "All"]
        ],
        "aoColumns": [
            { "width": "25%" },
            { "width": "40%" },
            { "width": "30%" },
            { "width": "5%", "orderable": false, "sClass": "text-center"}
        ],
        "columns": [
            null,
            null,
            null,
            { "searchable": false }
        ]
    });
}


function showAvailableMetrics() {
    $.get("servlets/getAvailableMetrics", {application: APP_ID}).done(function (entityList) {

        fillAvailableMetricsTable(entityList);

        $("#metrics-panel").on("click", function () {
            $("#app-available-metrics-collapsable").collapse("toggle");
        });
    }).fail(function (err) {
        $('#' + CONTENT_ID).html("<h1 class=\"text-center text-warning\">Invalid application.</h1>");
    }).always(function () {
        SPINNER.stop();
    })

}

function fillAvailableMetricsTable(entityList) {
    entityList.forEach(function (entity) {
        entity.metrics.forEach(function (metric) {
            var checkBox = "<input id=\"checkbox-" + APP_ID + "-" + entity.id + "-" + metric.name + "\" " +
                "type=\"checkbox\" onClick=\"if (this.checked) " +
                "{addGraph('" + APP_ID + "','" + entity.id + "','" + entity.name + "','" + metric.name + "') " +
                "} else { removeGraph('" + APP_ID + "','" + entity.id + "','" + metric.name + "')}\">";

            DATATABLE.row.add([entity.name, metric.name, metric.description, checkBox]);
        })
    })
    DATATABLE.draw();

}

function addGraph(appId, entityId, entityName, sensorName) {
    SPINNER.spin(document.getElementById("loading-spinner"));

    // Checking first if sensor is available by pulling a single value.
    var query = $.get('servlets/getMetricValueServlet', {application: appId, entity: entityId, sensor: sensorName});
    query.done(function () {
        new PNotify({
            title: 'New sensor added',
            text: 'Please scroll down to see it',
            type: 'info'
        });
        $('#page-graphs').append(generateSensorPanel(appId, entityId, entityName, sensorName));
        setupGraphs("#flot-line-chart-" + appId + "-" + entityId + "-" + sensorName, entityId, sensorName);
        $('#app-status-collapsable').collapse('hide')

    }).fail(function () {
        new PNotify({
            title: 'Error',
            text: 'The sensor you selected is not available at the moment',
            type: 'error'
        });
        $(escapeDots("#checkbox-" + appId + "-" + entityId + "-" + sensorName)).prop('checked', false);
    }).always(function () {
        SPINNER.stop();
    });

}

function removeGraph(appId, entityId, sensorName) {
    $(escapeDots("#panel-container-" + appId + '-' + entityId + '-' + sensorName)).remove();
    $('#app-status-collapsable').collapse('hide')

    for (var i = 0; i < ACTIVE_API_CALLS.length; i++) {
        if (ACTIVE_API_CALLS[i].functionName == appId + '-' + entityId + '-' + sensorName) {
            clearInterval(ACTIVE_API_CALLS[i].programedFunction);
            ACTIVE_API_CALLS.splice(i--, 1);
        }
    }
}


function generateSensorPanel(appId, entityId, entityName, sensorName) {

    var panelHeading = "<div class=\"col-lg-6\" id=\"panel-container-" + appId + "-" + entityId + "-" + sensorName + "\"" + "><div class=\"panel panel-default\">";
    panelHeading += "<div class=\"panel-heading\"><i class=\"fa fa-bar-chart-o fa-fw\"></i><strong> " + entityName +
        ", " + sensorName + "</strong> (" + entityId + ")</div>";


    var panelBody = "<div class=\"panel-body\">";


    panelBody += "<div class=\"flot-chart\"><div class=\"flot-chart-content\" id=\"flot-line-chart-" + appId + "-" + entityId + "-" + sensorName + "\"></div></div>";
    panelBody += "</div></div>";

    return  panelHeading + panelBody;

}


function setupGraphs(containerID, entityID, sensorName) {

    var container = $(escapeDots(containerID));

    series = [
        {
            lines: {
                fill: true
            }
        }
    ];

    var plot = $.plot(container, series, {
        grid: {
            borderWidth: 1,
            minBorderMargin: 25,
            labelMargin: 10,
            backgroundColor: {
                colors: ["#fff", "#e4f4f4"]
            },

            markings: function (axes) {
                var markings = [];
                var xaxis = axes.xaxis;
                for (var x = Math.floor(xaxis.min); x < xaxis.max; x += xaxis.tickSize * 2) {
                    markings.push({
                        xaxis: {
                            from: x,
                            to: x + xaxis.tickSize
                        },
                        color: "rgba(232, 232, 255, 0.2)"
                    });
                }
                return markings;
            }
        },
        xaxis: {
            tickFormatter: function () {
                return "";
            }
        },
        yaxis: {
            min: 0,
            ticks: 10,
            alignTicksWithAxis: 1
        },
        legend: {
            show: true
        }
    });

    var data = [];
    var MAXIMUM = container.outerWidth() / 2 || 300;

    // Programming updates
    function updateFunction(apiResponse) {
        var value = apiResponse;

        data.push(value);

        if (data.length >= MAXIMUM) {
            data.shift();
        }

        var graphPoints = [];
        for (var i = 0; i < data.length; ++i) {
            graphPoints.push([i, data[i]])
        }

        series[0].data = graphPoints;
        plot.setData(series);
        plot.autoScale();
        plot.draw();
    }

    var programedInterval = setInterval(function () {
        var query = $.get('servlets/getMetricValueServlet', {application: APP_ID, entity: entityID, sensor: sensorName}, updateFunction);
    }, 500);

    ACTIVE_API_CALLS.push({
        functionName: APP_ID + "-" + entityID + "-" + sensorName,
        programedFunction: programedInterval
    });


}

function escapeDots(myid) {
    return myid.replace(/(:|\.|\[|\])/g, "\\$1");
}