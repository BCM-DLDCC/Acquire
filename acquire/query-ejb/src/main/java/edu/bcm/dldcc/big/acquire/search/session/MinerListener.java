package edu.bcm.dldcc.big.acquire.search.session;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;


//import org.slf4j.Logger;
import org.jboss.logging.Logger;


import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperator;
import java.io.Serializable;


/**
 * This should be provided in {@link SearchManager}
 * @author amcowiti
 *
 */
@ConversationScoped
@Named("minerListener")
public class MinerListener implements Serializable {
	
	private static final long serialVersionUID = 2010746168237270621L;

	@Inject
	SearchManager searchManager; 
	
	public static Logger log = Logger.getLogger(MinerListener.class);
	
	
	private boolean showIt;
	
	 
	public void setShowIt(boolean showIt) {
		this.showIt = showIt;
	}
	public boolean isShowIt() {
		return showIt;
	}

	
	
	/**
	 * This temporary action should be removed
	 * @return
	 */
	public String collect(){
		log.info("@collect ...");//"miner?faces-redirect=true";
		return "miner";
	}

}
