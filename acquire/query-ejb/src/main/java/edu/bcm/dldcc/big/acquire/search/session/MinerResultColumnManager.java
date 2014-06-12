package edu.bcm.dldcc.big.acquire.search.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import edu.bcm.dldcc.big.acquire.listener.MinerSearchResultCoreField;
import edu.bcm.dldcc.big.acquire.listener.MinerSearchResultField;
import edu.bcm.dldcc.big.acquire.query.ColumnModel;
import edu.bcm.dldcc.big.acquire.query.MinerResultColumn;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;

/**
 * Miner Query Result UI Column Management
 * pew can find way to incorporate this into searchManager
 * @author amcowiti
 *
 */
@RequestScoped
@Named("minerResultColumn")
public class MinerResultColumnManager  implements MinerResultColumn,Serializable {

	private static final long serialVersionUID = -7779503338969008563L;
	
	public static Logger log = Logger.getLogger(MinerResultColumnManager.class);
	
	@Inject
	SearchManager searchManager; 
	
	private List<ColumnModel> columns = new ArrayList<ColumnModel>();
	private List<SearchFields<?, ?>> searchColumns= new ArrayList<SearchFields<?, ?>>(); 
	
	
	public List<SearchFields<?, ?>> getSearchColumns(){
		return this.searchColumns;
	}
	 
	 @Named
	 public List<ColumnModel> getColumns() {
	    	return columns;
	 }
	
	
	
	/**
	 * Add the core or "static" columns to dynamic columns list 
	 */
	public void addCoreColumnsToColumnList(){
		for(MinerSearchResultCoreField core:MinerSearchResultCoreField.values()) {
           columns.add(new ColumnModel(core.getLabel().toUpperCase(), core.getProperty()));
        }
	}
	
	/**
	 * Adds client/user selected Search Fields to Column List
	 */
	public void addSearchColumnsToColumnList(){
		
		MinerSearchResultField sf;
		for(SearchFields<?, ?> searchField : searchColumns) {
			//log.debug("@addSearchColumnsToColumnList current searchField="+searchField.toString());
			sf=MinerSearchResultField.getMinerSearchResultField(searchField);
			
			if(sf != null){
			  log.debug("@addSearchColumnsToColumnList found sf ="+sf.toString()+" (will not add) iscore?="+sf.isCore());
				if(!sf.isCore())
					columns.add(new ColumnModel(sf.getLabel().toUpperCase(), sf.getProperty()));
			}
	     }
	}
	
	
	    
		/**
		 * Determine list of client/user selected search Fields
		 */
		 public void determineSearchFields(){
			 
			 setSearchColumns(searchManager.getPatientFieldValues());
			  setSearchColumns(searchManager.getSpecimenFieldValues());
			  setSearchColumns(searchManager.getNormalFieldValues());
		}
		 
		 /**
		  * Prepare the search Column List
		  * @param map
		  */
		 private void setSearchColumns( Map<SearchFields<?, ?>, SearchCriteria<?>> map){
			//is there an Enum of search Fields
			 for(Map.Entry<SearchFields<?, ?>, SearchCriteria<?>> entry:map.entrySet()){
				 searchColumns.add(entry.getKey());
			 }
		 }
		 
		 /**
		  * Create Dynamic Columns for DataTable
		  */
		public void createDynamicColumns() {
	        columns.clear();      
	        addCoreColumnsToColumnList();
	        
	        //add other user selected, ignore the original six
	        determineSearchFields();
	        addSearchColumnsToColumnList();
	        
	    }
		
	    @PostConstruct
	    public void setup(){
	    	createDynamicColumns();
	    }
}
