package edu.bcm.dldcc.big.acquire.inventory.session;

/**
 * Log messages ( need to externalize these)
 * @author amcowiti
 *
 */
public enum NaLabDataUploadLogMessage {
	TOO_MANY_ROWS("The number of rows  exceeds the max allowed value of "),
ID_MISSING("Missing required sample ID or Patient UUID"),
ID_NOT_FOUND("The parent specimen for the supplied Id was not found."),
CHILD_LABEL_J_MISS("Child specimen Label J is missing"),
CHILD_LABEL_J_NOT_UNIQ("Child specimen Label J is not unique"),
RIN_VALUE_G_MISS("RIN value is Missing"),
RIN_VALUE_G_OUTTA_RANGE("The RIN Value is outside the range of 0.0-10.0"),
DNA_QUALITY_MISS("DNA Quality is missing"),
DNA_QUALITY_NOT_RECOGNIZED("Unrecognized DNA Quality."),
DERIVATIVE_TYPE_MISS("Missing Derivative Type."),
DERIVATIVE_TYPE_UNREGOGNIZED("Unrecognized Derivative Type Quality."),
CELL_BAD_FORMAT("The cell has a bad data format"),
AMOUNT_E("Amount is required"), 
AMOUNT_E_FORMAT("Amount specified in bad format"),
DERIVATIVE_EXISTS("The supplied Nucleic Acid Label already exists in the system"),
EXCEPTION_WITH_ROW("There was an error processing this row");

private String message;

private NaLabDataUploadLogMessage(String message) {
	this.message = message;
}

public String getMessage() {
	return message;
}

}
