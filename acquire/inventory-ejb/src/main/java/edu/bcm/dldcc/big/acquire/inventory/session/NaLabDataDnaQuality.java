package edu.bcm.dldcc.big.acquire.inventory.session;

/**
 * DNA Quality
 * @author amcowiti
 *
 */
public enum NaLabDataDnaQuality {
	HIHG("Not degraded"),
	MEDIUM("Partially degraded"),
	LOW("Degraded");
	
	private String quality;

	private NaLabDataDnaQuality(String quality) {
		this.quality = quality;
	}

	public String getQuality() {
		return quality;
	}
	
	

}
