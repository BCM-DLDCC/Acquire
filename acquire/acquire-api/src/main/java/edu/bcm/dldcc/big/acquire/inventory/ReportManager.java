/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.jms.JMSException;

import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.primefaces.event.TabChangeEvent;

import edu.bcm.dldcc.big.acquire.query.Reports;
import edu.bcm.dldcc.big.acquire.query.data.NaLabReportData;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.SearchResultDataModel;
import edu.bcm.dldcc.big.search.SearchFields;

/**
 * @author pew
 * 
 */
@Local
public interface ReportManager
{
  /**
   * Sets a value to be searched on, in combination with the specified field
   * 
   * @param term
   *          the term to be searched on
   */
  void setSearchTerm(Object term);

  /**
   * Gets the value that will be searched on
   * 
   * @return
   */
  Object getSearchTerm();

  /**
   * Returns the selection of fields that can be searched on for reports.
   * 
   * @return
   */
  Set<SearchFields<?, ?>> getSearchFields();

  /**
   * Sets the search field to be used in the report
   * 
   * @param fields
   */
  void setSearchField(SearchFields<?, ?> fields);

  /**
   * Gets the search field used for the report search
   * 
   * @return
   */
  SearchFields<?, ?> getSearchField();

  /**
   * Run a search in conjunction with a report.
   * 
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  List<SearchResult> runSearch() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Retrieves the search results
   * 
   * @return
   */
  List<SearchResult> getSearchResults();

  /**
   * Retrieves the list of notifications/todo list. Available in EL as
   * "notifications"
   * 
   * @return
   * @throws FeatureNotSupportedException 
   * @throws IdentityException 
   * 
   * @see #fetchNotifications()
   */
  SearchResultDataModel getNotifications() throws IdentityException, FeatureNotSupportedException;

  /**
   * Add to the list of notifications/todo list. Not to be used at the current
   * time
   * 
   * @param add
   * @throws IdentityException
   */
  void addNotification(SearchResult add) throws IdentityException;

  /**
   * Removes notifications from the list with the current status. Not
   * implemented currently
   * 
   * @param remove
   */
  void removeNotification(SearchResult remove);

  /**
   * Retrieves the notification specimen into memory
   * @throws FeatureNotSupportedException 
   * @throws IdentityException 
   */
  void fetchNotifications() throws IdentityException, FeatureNotSupportedException;

  /**
   * Get the collection site to be used in searches
   * 
   * @return
   */
  String getCollectionSite();

  /**
   * Set the collection site to be searched on, in conjunction with the set
   * search field
   * 
   * @param site
   */
  void setCollectionSite(String site);

  /**
   * Get the choices for Overview Report types.
   * 
   * @return
   */
  List<Reports> getReportTypes();

  /**
   * Set the overview report to be run
   * 
   * @param type
   */
  void setReportType(Reports type);

  /**
   * Get the overview report to be run
   * 
   * @return
   */
  Reports getReportType();

  /**
   * Get the result of the QC report.
   * 
   * @return
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   * @throws SecurityException
   * 
   * @see {@link #runQCReport()}
   */
  List<SearchResult> getQcReport() throws IdentityException, SecurityException,
      FeatureNotSupportedException;

  /**
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   * 
   */
  void runPathologyReport() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Retrieves the specimen results of a Pathology Report.
   * 
   * @return
   * @see #runPathologyReport()
   */
  SearchResultDataModel getPathologyReport();

  /**
   * Retrieves the aliquot results of a Pathology Report.
   * 
   * @return
   * @see #runPathologyReport()
   */
  SearchResultDataModel getAliquotReport();

  /**
   * Gets the filtered list of notifications
   * @return
   * 
   * @see #getNotifications()
   */
  List<SearchResult> getFilteredNotifications();

  /**
   * Sets the filtered list of notifications
   * 
   * @param filteredNotifications
   *
   * @see #getNotifications()
   */
  void setFilteredNotifications(List<SearchResult> filteredNotifications);
  
  /**
   * Saves updates made to the specimen in the pathology report to the 
   * datastore, clears all data, and ends the current conversation.
   * 
   * @see #runPathologyReport()
   */
  void saveUpdates();

  /**
   * Saves updates made to the specimen in the pathology report to the
   * datastore and leaves the state intact for additional changes
   * 
   * @see #runPathologyReport()
   */
  void applyUpdates();

  /**
   * Runs a search to retrieve specimen based on user criteria for
   * potential inclusion in a shipping report. This search will include
   * normal specimen as well as aliquots.
   * 
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  void runShipmentFormReport() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Runs a search to retrieve specimen meeting the criteria of one of the 
   * pre-defined QC reports. 
   * 
   * @throws IdentityException
   * @throws SecurityException
   * @throws FeatureNotSupportedException
   */
  void runQCReport() throws IdentityException, SecurityException,
      FeatureNotSupportedException;
  
  /**
   * Retrieves the result of a search for specimen intended for a shipment form.
   * 
   * @return
   */
  SearchResultDataModel getShipmentReport();
  
  /**
   * Clears the results of the previous shipment form specimen search.
   */
  void clearShipmentReport();
  
  /**
   * Marks the provided Speicmen as having been shipped to TCGA.
   * 
   * @param specimen
   */
  void shipToTcga(SearchResult specimen);
  
  /**
   * Retrieve the results of a NA Lab Report search. The results for this
   * report are given in a different view of the underlying specimen than 
   * the other reports, based on the derivatives produced by the NA Lab from
   * a specimen, rather than the actual specimen.
   * 
   * @return
   * @throws IdentityException
   * @throws SecurityException
   * @throws FeatureNotSupportedException
   */
  List<NaLabReportData> getNaLabReport() throws IdentityException, SecurityException, FeatureNotSupportedException;
  
  /**
   * Retrieves the list of NA Lab report data for use in filtering the results.
   * @return
   */
  List<NaLabReportData> getFilteredNaLabReport();
  
  /**
   * Sets the List of NA Lab report data that contains the filtered results.
   * 
   * @param data
   */
  void setFilteredNaLabReport(List<NaLabReportData> data);
  

}
