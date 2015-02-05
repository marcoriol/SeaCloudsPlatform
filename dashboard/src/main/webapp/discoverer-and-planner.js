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