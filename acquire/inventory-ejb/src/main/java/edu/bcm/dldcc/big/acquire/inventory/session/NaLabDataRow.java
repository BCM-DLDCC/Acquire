package edu.bcm.dldcc.big.acquire.inventory.session;

import java.math.BigDecimal;

import edu.bcm.dldcc.big.acquire.inventory.data.NaLabData;
import edu.bcm.dldcc.big.clinical.data.DerivativeType;
import edu.bcm.dldcc.big.clinical.data.DnaQuality;

/**
 * Simple row "javaBean"
 * @author amcowiti
 *
 */
public class NaLabDataRow {

	private Integer row;
	private String parentId;
	private String patientId;
	
	private String dateReceived;//C anno
	private String amountConsumed;//D anno
	private String amount;//E
	private  String concentration;//F
	private String rin;//G
	private BigDecimal rinBg;
	private String quality;//H
	private DnaQuality dnaQuality;
	private String type;//I anno
	private DerivativeType derivativeType;
	private String derivativeId;//J
	
	private NaLabData naLabData;
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getDateReceived() {
		return dateReceived;
	}
	public void setDateReceived(String dateReceived) {
		this.dateReceived = dateReceived;
	}
	public String getAmountConsumed() {
		return amountConsumed;
	}
	public void setAmountConsumed(String amountConsumed) {
		this.amountConsumed = amountConsumed;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getConcentration() {
		return concentration;
	}
	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}
	public String getRin() {
		return rin;
	}
	public void setRin(String rin) {
		this.rin = rin;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDerivativeId() {
		return derivativeId;
	}
	public void setDerivativeId(String derivativeId) {
		this.derivativeId = derivativeId;
	}
	public BigDecimal getRinBg() {
		return rinBg;
	}
	public void setRinBg(BigDecimal rinBg) {
		this.rinBg = rinBg;
	}
	public DerivativeType getDerivativeType() {
		return derivativeType;
	}
	public void setDerivativeType(DerivativeType derivativeType) {
		this.derivativeType = derivativeType;
	}
	public NaLabData getNaLabData() {
		return naLabData;
	}
	public void setNaLabData(NaLabData naLabData) {
		this.naLabData = naLabData;
	}
	public DnaQuality getDnaQuality() {
		return dnaQuality;
	}
	public void setDnaQuality(DnaQuality dnaQuality) {
		this.dnaQuality = dnaQuality;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	
}
