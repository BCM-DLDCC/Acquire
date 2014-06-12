/*
 *
 *general functions for the Acquire site
 *
 *
 */

//check and see if any dataTable has rows being actively edited
function checkDataTableEdit() {
	// basically, look for any element with a class of ui-cell-editor-input and
	// display:inline

	if ($("span.ui-cell-editor-input:visible").length) {
		return true;
	} else {
		return false;
	}

}

// function to fire when pathology forms are being edited
// it will block submission if any live edits are being made
function clearPathologySubmission() {

	// do we have any live unsaved edits?
	if (checkDataTableEdit()) {
		alert("There are unsaved row edits. Please save or cancel them before proceeding.");
		return false;
	} else {
		return true;
	}

}

// show/hide the dropdown for QC Report Sites
function toggleReportSites() {

	// the selected report type value
	var reportType = (qcReportSelector.getSelectedValue());

	// get the displayed value of the site dropdown
	var displayStatus = $("#qcReportsDiv").css('display');

	// if the report type is the one to display the sites and the sites are
	// hidden, display them
	if ((reportType == "QC_REPORT" || reportType == "NA_LAB_REPORT")
			&& displayStatus == "none") {
		$("#qcReportsDiv").fadeIn(250);
	}

	// if the report type is the one to hide the sites and the sites are
	// displayed, hide them
	if ((reportType != "QC_REPORT" && 
			reportType !== "NA_LAB_REPORT")
			&& displayStatus != "none") {
		$("#qcReportsDiv").fadeOut(250);
	}
}

// tweaks to the charts
function chartTweak() {

	// set the canvas to white
	this.cfg.grid = {

		background : "#FFFFFF"

	};
}

// disable form submissions when enter is pressed
// this must be called when the page is updated with AJAX; otherwise, it won't
// be bound to the form elements
function disableEnterSubmit() {

	$("form").bind("keypress", function(e) {
		if (e.keyCode == 13)
			return false;
	});
}
