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
var SPINNER = new Spinner({lines: 13, length: 6, width: 2, radius: 5, top: "-5px"});
var CONTENT_ID = "page-content";

$(document).ready(function () {
    PNotify.prototype.options.styling = "fontawesome";

    $("#iframe").attr("src", "slagui/agreements/");
    $('#iframe').load(function () {
        // Workaround to detect if the IFrame failed
        var iframe = $("#iframe");
        if (iframe.contents().text().search("404") != -1) {
            $('#' + CONTENT_ID).html("<h1 class=\"text-center text-danger\">SLA module is not available in this moment.</h1>");
        }
        SPINNER.stop();
    });


    $("#sla-agreement-heading").on("click", function () {
        $("#sla-form-collapsable").collapse("toggle");
    });

    $("#iframe").attr("src", "/projects/seaclouds/slagui/agreements/");
    setInterval(updatePage, 10000);


});

function updatePage() {
    SPINNER.spin(document.getElementById("loading-spinner"));
    document.getElementById('iframe').contentWindow.location.reload(true);
}


function addSlaAgreement() {
    var templateNameInput = $("#templateNameInput").val();
    var appIdInput = $("#appIdInput").val();
    var moduleIdInput = $("#moduleIdInput").val();
    var consumerId = (templateNameInput == "nuro-cloud-template") ? "nuro" : "any-client";
    var data = {
        templateid: templateNameInput,
        consumerid: consumerId,
        appid: appIdInput,
        moduleid: moduleIdInput
    };

    $.ajax({
        url: "/projects/seaclouds/slagui/rest/agreements",
        type: "POST",
        data: JSON.stringify(data), /* this should not be needed */
        contentType: "application/json",
        dataType: "json"
    }).done(function (response) {
        $.ajax({
            url: '/projects/seaclouds/slagui/rest/enforcements/' + response.id,
            type: 'PUT',
            dataType: 'text'
        }).done(function (res) {
            new PNotify({
                title: 'New agreement created',
                text: 'Please wait until it appear on the list',
                type: 'success'
            });
            updatePage();
        }).fail(function (err) {
            new PNotify({
                title: 'Error',
                text: 'The SLA Module failed while adding a new enforcement',
                type: 'error'
            });
        });
    }).fail(function (err) {
        new PNotify({
            title: 'Error',
            text: 'The SLA Module failed while adding a new agreement',
            type: 'error'
        });
    })

}