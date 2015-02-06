/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
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
var CONTENT_ID = "deployed-applications";
var APP_DEPLOYER_TEXTAREA;

$(document).ready(function () {
    $("#app-status-heading").on("click", function () {
        $("#app-status-collapsable").collapse("toggle");
    });

    PNotify.prototype.options.styling = "fontawesome";

    setupTextArea();
    updatePage();
    setInterval(updatePage, 10000);
});

function setupTextArea() {
    var appDeployer = document.getElementById("yaml-input-textarea");
    APP_DEPLOYER_TEXTAREA = CodeMirror.fromTextArea(appDeployer, {scrollbarStyle: "simple", tabSize: 2});
    APP_DEPLOYER_TEXTAREA.setSize("100%", 300);

    $("#app-status-collapsable").on('shown.bs.collapse', function () {
        if (APP_DEPLOYER_TEXTAREA.getValue().length == 0) {
            APP_DEPLOYER_TEXTAREA.setValue(" ");
        }
        APP_DEPLOYER_TEXTAREA.focus();
    })

    $("#yaml-input-file").on("change", function () {
        var unparsedFile = $("#yaml-input-file").prop('files')[0];
        var reader = new FileReader();

        reader.onload = function (theFile) {
            APP_DEPLOYER_TEXTAREA.setValue(theFile.target.result);
        }
        reader.readAsText(unparsedFile);
    })
}

function updatePage() {
    SPINNER.spin(document.getElementById("loading-spinner"));
    displayApplicationOverview();
}

// Deployed applications

function displayApplicationOverview() {
    var boxHTML = "";
    $.get("servlets/listApplications", function getApplications(response) {
        if (response.length > 0) {
            $.each(response, function (appIdx, app) {
                boxHTML += generateAppOverviewBox(app);
            })
        } else {
            boxHTML = "<h1 class=\"text-center text-warning\">No applications running.</h1>";
        }
    }).done(function () {
        $('#' + CONTENT_ID).html(boxHTML);
    }).fail(function () {
        boxHTML = "<h1 class=\"text-center text-danger\">Deployer module is not available in this moment.</h1>";
        $('#page-content').html(boxHTML)
    }).always(function () {
        $('#' + CONTENT_ID).show();
        SPINNER.stop();
    });
}

function generateAppOverviewBox(application) {
    // External container
    var appHTML = "<div class=\"col-lg-6\"><div class=\"panel panel-default\">";

    // Header
    appHTML += "<div class=\"panel-heading clearfix\">";
    appHTML += "<i class=\"fa fa-gears fa-fw\"><\/i> " + application.spec.name +
        "<button type=\"button\" class=\"btn btn-danger navbar-right\" onClick=deleteApp('" + application.id + "')>Remove</button>";
    appHTML += "</div>";

    // Body
    appHTML += "<div class=\"panel-body\" id=\"information-panel\">";
    appHTML += "<strong>ID: </strong> " + application.id + "<br>";
    appHTML += "<strong>Name: </strong> " + application.spec.name + "<br>";
    appHTML += "<strong>Type: </strong> " + application.spec.type + "<br>";

    if (application.status == "RUNNING") {
        appHTML += "<strong>Status: </strong> <span class=\"text-success\">" + application.status + "</span><br>";
    } else if (application.status == "STARTING") {
        appHTML += "<strong>Status: </strong> <span class=\"text-muted\">" + application.status + "</span><br>";
    } else {
        appHTML += "<strong>Status: </strong> <span class=\"text-danger\">" + application.status + "</span><br>";
    }

    appHTML += "<ul class=\"list-unstyled\"><li><strong>Application Components</strong></li>";
    application.descendants.forEach(function (descendant) {
        appHTML += "<li role=\"presentation\" class=\"dropdown-header\">" +
            "<a href=\"#\" data-toggle=\"collapse\" class=\"active\" data-target=\"#" + descendant.id + "-collapse\">" +
            "<i class=\"fa fa-search\"></i> " + descendant.name + " Summary  <i class=\"fa fa-angle-down\"></i></a></li>";

        var collapseStatus = $("#" + descendant.id + "-collapse");
        if (collapseStatus && collapseStatus.hasClass("in")) {
            appHTML += "<li id=\"" + descendant.id + "-collapse\" class=\"collapse in\">";
        } else {
            appHTML += "<li id=\"" + descendant.id + "-collapse\" class=\"collapse\">";
        }

        appHTML += "<ul  class=\"list-unstyled\" style=\"padding-left : 5%\">";
        appHTML += "<li><i class=\"fa fa-chevron-right\"></i> <strong>ID: </strong>" + descendant.id + "</li>";
        appHTML += "<li><i class=\"fa fa-chevron-right\"></i> <strong>Name: </strong>" + descendant.name + "</li>";
        appHTML += "<li><i class=\"fa fa-chevron-right\"></i> <strong>Type: </strong>" + descendant.type + "</li>";
        appHTML += "<li role=\"presentation\" class=\"dropdown-header\">" +
            "<a href=\"#\" data-toggle=\"collapse\" class=\"active\" data-target=\"#" + descendant.id + "-locations-collapse\">" +
            "<i class=\"fa fa-map-marker\"></i> Locations <i class=\"fa fa-angle-down\"></i></a>";

        collapseStatus = $("#" + descendant.id + "-locations-collapse");
        if (collapseStatus && collapseStatus.hasClass("in")) {
            appHTML += "<li id=\"" + descendant.id + "-locations-collapse\" class=\"collapse in\">";
        } else {
            appHTML += "<li id=\"" + descendant.id + "-locations-collapse\" class=\"collapse\">";
        }

        appHTML += "<ul  class=\"list-unstyled\" style=\"padding-left : 5%\">";

        descendant.locations.forEach(function (location) {
            appHTML += "<li><i class=\"fa fa-chevron-right\"></i> <strong>ID: </strong>" + location.id + "</li>";
            appHTML += "<li><i class=\"fa fa-chevron-right\"></i> <strong>Name: </strong>" + location.name + "</li><br/>";
        })

        appHTML += "</ul>";
        appHTML += "</li>";
        appHTML += "</ul>";

    })
    appHTML += "</ul>"
    appHTML += "<br>";
    appHTML += "</div>";

    // External container
    appHTML += "</div>";
    appHTML += "</div>";

    return appHTML
}


function deleteApp(applicationId) {
    // http://stackoverflow.com/a/15089299
    SPINNER.spin(document.getElementById("loading-spinner"));
    $.ajax({
        url: "servlets/removeApplication" + "?" + $.param({application: applicationId}),
        type: 'DELETE'
    }).done(function () {
        new PNotify({
            title: 'App removed successfully',
            text: 'Please wait until the application list refreshes',
            type: 'success'
        });
        setTimeout(function () {
            updatePage();
        }, 1000);
    }).fail(function () {
        new PNotify({
            title: 'Error',
            text: 'Something happened while removing the application',
            type: 'error'
        });
    }).always(function () {
        SPINNER.stop();
    })

    return false;
}

function submitYaml() {
    SPINNER.spin(document.getElementById("loading-spinner"));
    $("#app-status-collapsable").collapse("toggle");

    var query = $.post("servlets/addApplication", {yaml: APP_DEPLOYER_TEXTAREA.getValue()});
    query.done(function () {
        APP_DEPLOYER_TEXTAREA.setValue(" ");
        new PNotify({
            title: 'App deployed successfully',
            text: 'It will appear soon on the application list',
            type: 'success'
        });
        setTimeout(function () {
            updatePage();
        }, 1000);
    }).fail(function () {
        new PNotify({
            title: 'Error',
            text: 'Something happened while deploying the application',
            type: 'error'
        });
    }).always(function () {
        SPINNER.stop();
    })
    return false;
}