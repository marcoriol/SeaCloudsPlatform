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
var SPINNER = new Spinner({lines: 13, length: 6, width: 2, radius: 5, top: "-5px"});
var APP_TOPOLOGY_TEXTAREA;
var PLANNER_OUTPUT_TEXTAREA;


$(document).ready(function () {
    loadCodeMirror();

    PNotify.prototype.options.styling = "fontawesome";

    $("#yaml-input-file").on("change", function () {
        var unparsedFile = $("#yaml-input-file").prop('files')[0];
        var reader = new FileReader();

        reader.onload = function (theFile) {
            APP_TOPOLOGY_TEXTAREA.setValue(theFile.target.result);
        };
        reader.readAsText(unparsedFile);
    });
});

function loadCodeMirror() {
    SPINNER.spin(document.getElementById("loading-spinner"));
    var appTopology = document.getElementById("app-topology-textarea");
    var plannerOutput = document.getElementById("planner-output");

    APP_TOPOLOGY_TEXTAREA = CodeMirror.fromTextArea(appTopology, {scrollbarStyle: "simple", tabSize: 2})
    APP_TOPOLOGY_TEXTAREA.setSize("100%", 400);

    APP_TOPOLOGY_TEXTAREA.setValue("Write your YAML here...");
    APP_TOPOLOGY_TEXTAREA.focus();

    PLANNER_OUTPUT_TEXTAREA = CodeMirror.fromTextArea(plannerOutput, {readOnly: true, scrollbarStyle: "simple", tabSize: 2})
    PLANNER_OUTPUT_TEXTAREA.setSize("100%", 400);

    PLANNER_OUTPUT_TEXTAREA.setValue("");

    SPINNER.stop();

}

function processAppTopology() {
    SPINNER.spin(document.getElementById("loading-spinner"));

    $.get("servlets/getMatchmaking", {yaml: APP_TOPOLOGY_TEXTAREA.getValue()}, function getMatchmaking(response) {
        PLANNER_OUTPUT_TEXTAREA.setValue(response);
        SPINNER.stop();
    }).fail(function () {
        new PNotify({
            title: 'Error',
            text: 'Error processing the application model',
            type: 'error'
        });
        SPINNER.stop();
    });
}