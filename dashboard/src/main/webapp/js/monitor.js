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
var DATATABLE;
var CONTENT_ID = "page-content";


$(document).ready(function () {
    setupDataTable();
    updatePage();
    setInterval(updatePage, 3000);
});

function setupDataTable() {
    DATATABLE = $('#application-table').DataTable({
        responsive: true,
        "lengthMenu": [
            [20, 50, -1],
            [20, 50, "All"]
        ],
        "aoColumns": [
            null,
            null,
            null,
            null,
            { "orderable": false }
        ],
        "columns": [
            null,
            null,
            null,
            null,
            { "searchable": false }
        ]
    });
}

function updatePage() {
    SPINNER.spin(document.getElementById("loading-spinner"));

    $.get("servlets/listApplications", function (applicationList) {
        if (applicationList.length > 0) {
            fillAvailableApplicationsTable(applicationList);
        }
    }).fail(function () {
        boxHTML = "<h1 class=\"text-center text-danger\">Monitor module is not available in this moment.</h1>";
        $('#' + CONTENT_ID).html(boxHTML)
    }).always(function () {
        SPINNER.stop();
    });

}

function fillAvailableApplicationsTable(applicationList) {
    DATATABLE.clear();

    applicationList.forEach(function (application) {
        var button;
        var status;
        if (application.status == "RUNNING") {
            button = "<a href=\"app-monitor.html?id=" + application.id + "\">" +
                "<button type=\"button\" class=\"btn btn-info\">Live monitor</button></a>";
            status = "<span class=\"text-success\"><strong>" + application.status + "</strong></span>";
        } else if (application.status == "STARTING") {
            button = "<button type=\"button\" class=\"btn btn-info\" disabled>Live monitor</button>";
            status = "<span class=\"text-muted\"><strong>" + application.status + "</strong></span><br>";
        } else {
            button = "<button type=\"button\" class=\"btn btn-info\" disabled>Live monitor</button>";
            status = "<span class=\"text-danger\"><strong>" + application.status + "</strong></span><br>";
        }

        DATATABLE.row.add([application.id, application.spec.name, application.spec.type, status, button]);

    })
    DATATABLE.draw();

}