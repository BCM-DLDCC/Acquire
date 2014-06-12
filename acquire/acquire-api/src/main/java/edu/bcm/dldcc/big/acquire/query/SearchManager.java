/**
 * 
 */
package edu.bcm.dldcc.big.acquire.query;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.ShipmentSearchFields;
import edu.bcm.dldcc.big.acquire.shipment.entity.Shipment;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.bcm.dldcc.big.search.SearchOperation;

/**
 * @author pew
 * 
 */
@Local
public interface SearchManager
{
	//TODO apollo  remove or merged with rest of API
	public Long maxUiResults();
	public String getTerms();
	public void reset();
	//apollo
	
  Long runCount();

  List<SearchResult> runQuery();

  SearchOperation getOperation();

  void setOperation(SearchOperation operation);

  <T> List<T> getSpecimenFieldValues(SearchFields<?, ?> field, Class<T> type);

  <T> List<T> getPatientFieldValues(SearchFields<?, ?> field, Class<T> type);

  <T> List<T> getNormalFieldValues(SearchFields<?, ?> field, Class<T> type);

  void addSpecimenSearchField(SearchFields<?, ?> field);

  void addPatientSearchField(SearchFields<?, ?> field);

  void addNormalSearchField(SearchFields<?, ?> field);

  void removeSpecimenSearchField(SearchFields<?, ?> field);

  void removePatientSearchField(SearchFields<?, ?> field);

  void removeNormalSearchField(SearchFields<?, ?> field);

  void toggleSpecimenSearchField(SearchFields<?, ?> field)
      throws SecurityException, IllegalArgumentException,
      InstantiationException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException;

  void togglePatientSearchField(SearchFields<?, ?> field)
      throws SecurityException, IllegalArgumentException,
      InstantiationException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException;

  void toggleNormalSearchField(SearchFields<?, ?> field)
      throws SecurityException, IllegalArgumentException,
      InstantiationException, IllegalAccessException, NoSuchMethodException,
      InvocationTargetException;

  SearchFields<?, ?> getSearchField();

  void setSearchField(SearchFields<?, ?> field);

  List<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>> getSpecimenEntries();

  List<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>> getPatientEntries();

  List<Map.Entry<SearchFields<?, ?>, SearchCriteria<?>>> getNormalEntries();

  List<SearchFields<?, ?>> getSpecimenFields();

  List<SearchFields<?, ?>> getPatientFields();

  List<SearchFields<?, ?>> getNormalFields();

  Map<SearchFields<?, ?>, SearchCriteria<?>> getSpecimenFieldValues();

  Map<SearchFields<?, ?>, SearchCriteria<?>> getPatientFieldValues();

  Map<SearchFields<?, ?>, SearchCriteria<?>> getNormalFieldValues();

  void clearFields();

  void clearResults();

  Long getQueryCount();

  List<SearchResult> getQueryResult();

  void addAfterSubmitDateSearch(Date timestamp);

  void addBeforeSubmitDateSearch(Date timestamp);

  void addSiteQuery(SiteAnnotation site);
  
  /**
   * @return the includeAliquots
   */
  Boolean getIncludeAliquots();

  /**
   * @param includeAliquots the includeAliquots to set
   */
  void setIncludeAliquots(Boolean includeAliquots);

  /**
   * @return the includeNormals
   */
  Boolean getIncludeNormals();

  /**
   * @param includeNormals the includeNormals to set
   */
  void setIncludeNormals(Boolean includeNormals);
  
  Boolean getIncludeClosedOrDisabled();
  
  void setIncludeClosedOrDisabled(Boolean includeClosedDisabled);
  
  List<Shipment> runShipmentSearch(ShipmentSearchFields field, SearchCriteria<?> values);

}
