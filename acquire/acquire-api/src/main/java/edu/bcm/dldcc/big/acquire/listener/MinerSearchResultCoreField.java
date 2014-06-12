package edu.bcm.dldcc.big.acquire.listener;

import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.search.SearchFields;

/**
 * This Enum is probably not necessary
 * @author amcowiti
 *
 */
public enum MinerSearchResultCoreField {
	
	S_LABEL(SpecimenSearchFields.LABEL,"Specimen Label","specimenLabel"),
	P_UUID(null,"Patient UUID","uuid"),
	P_SCOL_SITE(SpecimenSearchFields.SPECIMEN_COLLECTION_SITE,"Collection site","collectionSite"),
	S_TYPE(SpecimenSearchFields.TUMOR_TYPE,"Specimen Type","specimenType"),//or ?SpecimenSearchFields.TYPE
	P_D_DIAG(SpecimenSearchFields.DIAGNOSIS,"Disease Diagnosis","diseaseDiagnosis"),
	P_D_SITE(SpecimenSearchFields.DISEASE_SITE,"Disease site","diseaseSite");
	
	private SearchFields<?, ?> field; 
	private String label; 
	private String property; 
	
	  private MinerSearchResultCoreField(SearchFields<?, ?> field,String label,String property)
	  {
		this.field = field;
	    this.label=label;
	    this.property=property;
	  }
	  
	public String getProperty() {
		return property;
	}


	public SearchFields<?, ?> getField() {
		return field;
	}

	public String getLabel() {
		return label;
	}



	@Override
	  public String toString()
	  {
	    return this.label;
	  }
}
