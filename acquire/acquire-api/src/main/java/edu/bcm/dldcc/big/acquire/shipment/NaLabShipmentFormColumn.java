package edu.bcm.dldcc.big.acquire.shipment;

/**
 * Enum representing Shipment form Excel file Columns
 * @author amcowiti
 *
 */
public enum NaLabShipmentFormColumn {

	BOX_LOCATION(0,"Box location","Only applicable if samples are submitted in a box format.",NaLabShipmentFormColor.RED),
	DISEASE_TYPE(1,"Disease type",null,null),
	TUBE_BAR_CODE(2,"Tube bar code","The scannable 2-D tube barcode.   Also attach a human readable label to the side of each tube that corresponds to your \"Sample ID\" (Column E).",NaLabShipmentFormColor.GREEN),
	SAMPLE_ID(3,"Sample ID","The sample identifier that you (the collaborator) use in your laboratory to uniquely identify the sample.",null),
	INDIVIDUAL_ID(4,"Individual ID","The anonymized identifier that you (the collaborator) use to denote a particular individual (i.e. patient sequence number, UUID,  etc). Note that a tumor-normal pair from the same individual *must* share the same individual id but have distinct sample ids",null),
	SAMPLE_TYPE(5,"Sample Type","Select the appropriate type from dropdown list",null),
	PATIENT_ROLE(6,"Patient Role","The role of the patient in relation to the project.  Select the appropriate role from dropdown list",null),
	RELATIONSHIP_WITH_PROBAND(7,"Relationship with Proband","The role of the sample in relation to the proband. ",null),
	COLLECTION_DATE(8,"Collection Date","The date that you (the collaborator) use to reference when the sample was collected.",null),
	PRESERVATIVE(9,"Preservative","Identify the preservative in which the tissue is presently stored",null),
	AMOUNT_LIQUIID(10,"Estimated amount of liquid in the tube (ml)","Volume/tube in ml ",null),
	SAMPLE_COLLECTION_SITE(11,"Sample Collection Site","Tissue type and/or anatomical location from which the sample was obtained.",null),
	AMOUNT_CELLS_OR_TUMOR(12,"Estimated amount of cells or tumor in the tube (mg)","mg of tissue or amount of  cells",null),
	COMMENTS(13,"Comments","Optional non-identifying information about the sample.",null);
	
	private Integer col;
	private String column;
	private String desc;
	private NaLabShipmentFormColor color;
	
	private NaLabShipmentFormColumn(Integer col, String column, String desc,NaLabShipmentFormColor color) {
		this.col = col;
		this.column = column;
		this.desc = desc;
		this.color=(color==null)?NaLabShipmentFormColor.DEFAULT:color;
	}
	public Integer getCol() {
		return col;
	}
	public String getColumn() {
		return column;
	}
	public String getDesc() {
		return desc;
	}
	public NaLabShipmentFormColor getColor() {
		return color;
	}
	
	
}
