package edu.bcm.dldcc.big.acquire.listeners;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import edu.bcm.dldcc.big.rac.data.Degree;
import edu.bcm.dldcc.big.rac.data.InstitutionType;
import edu.bcm.dldcc.big.search.SearchOperation;

/**
 * Provide search operator options
 * This should probably be in {@link SearchManager}
 * @author amcowiti
 *
 */
@Model
public class SearchOperatorBean {

	 @Produces
	public SearchOperation[] getOptions(){
		return SearchOperation.values();
	}
	 
	 @Produces
	 public Degree[] getDegrees(){
		 return Degree.values();
	 }
	 
	 @Produces
	 public InstitutionType[] getInstitutionTypes(){
		 return InstitutionType.values();
	 }
}
