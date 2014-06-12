package edu.bcm.dldcc.big.acquire.shipment;

/**
 * Example Samples
 * @author amcowiti
 *
 */
public enum ExampleSample {
	BOXE("Example"),BOX1("X1"),BOX2("X2"),
	DSE("Example"),DS2("pancreatic exocrine adenocarcinoma"),DS3("X disorder"),
	BAR("A22222222"),
	SAMPLEID("09-33450-01"),SAMPLEID2("10-36545.20"),
	PATID("PACA 30"),PATID2("HS_230_902"),
	SAMPLETYPE("Recurrent Blood Derived Cancer"),SAMPLETYPE2("Recurrent Tumor"),
	ROLE("Affected"),
	RELATIONSHIP("Proband"),RELATIOSHIP2("Sister"),
	DATE("9/10/10"),
	PRESERVE("PAXgene"),
	LIQ("1 ml"),
	COL("Pancreas"),
	TUMOR("100 mg");
private String sample;
public String getSample() {
	return sample;
}

private ExampleSample(String sample) {
	this.sample = sample;
}

@Override
public String toString() {
	return sample;
}

}
