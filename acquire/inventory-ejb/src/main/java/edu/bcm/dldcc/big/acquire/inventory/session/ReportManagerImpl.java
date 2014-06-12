/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.session;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.AttributesManager;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.admin.PermissionManager;
import edu.bcm.dldcc.big.acquire.inventory.ReportManager;
import edu.bcm.dldcc.big.acquire.inventory.manager.CaTissueMonitor;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.qualifiers.OperationsLiteral;
import edu.bcm.dldcc.big.acquire.query.Reports;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.AliquotSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.NaLabReportData;
import edu.bcm.dldcc.big.acquire.query.data.ParticipantSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.query.data.SearchResultDataModel;
import edu.bcm.dldcc.big.acquire.query.data.SpecimenSearchFields;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchFields;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * @author pew
 * 
 */
@Stateful
@ConversationScoped
@Named("reportManager")
public class ReportManagerImpl implements ReportManager
{

  @Inject
  private SearchManager searchEngine;

  @Inject
  private IdentitySession identitySession;

  @Inject
  private Identity identity;

  @Inject
  private PermissionManager permissions;

  @Inject
  private Utilities util;

  @Inject
  private CaTissueMonitor monitor;

  @Inject
  @Any
  private Event<SearchResult> specimenEvent;

  @Inject
  @Named("updateStatus")
  private Set<SpecimenStatus> updates;

  private Object searchTerm;

  private String collectionSite;

  private SearchFields<?, ?> field;

  private List<SearchResult> searchResults = new ArrayList<SearchResult>();

  private List<SearchResult> pathologyReport = new ArrayList<SearchResult>();

  private SearchResultDataModel pathologyDataModel = new SearchResultDataModel(
      new ArrayList<SearchResult>());

  private SearchResultDataModel aliquotDataModel = new SearchResultDataModel(
      new ArrayList<SearchResult>());

  private SearchResultDataModel shipmentDataModel = new SearchResultDataModel(
      new ArrayList<SearchResult>());

  private List<SearchResult> qcReport = new ArrayList<SearchResult>();

  private List<SearchResult> aliquotReport = new ArrayList<SearchResult>();

  private SearchResultDataModel notifications = new SearchResultDataModel(
      new ArrayList<SearchResult>());

  private List<SearchResult> filteredNotifications;

  private List<NaLabReportData> filteredNaLabReport;

  private List<NaLabReportData> naLabReport = new ArrayList<NaLabReportData>();

  private Reports reportType;

  private static final String ADD_ATTRIBUTE_NAME = "ADDITIONAL_NOTIFICATIONS";

  private static final String REMOVE_ATTRIBUTE_NAME = "REMOVED_NOTIFICATIONS";

  @Inject
  @Annotations
  @Operations
  private EntityManager em;

  @Inject
  @Any
  private Instance<EntityManager> caTissueEms;

  @Inject
  private Messages messages;

  @Inject
  @Named("naLabReport")
  private List<NaLabReportData> naLabReportBase;

  /**
   * 
   */
  public ReportManagerImpl()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.ReportManager#setSearchTerm(java.lang
   * .String)
   */
  @Override
  public void setSearchTerm(Object term)
  {
    this.searchTerm = term;

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#getSearchTerm()
   */
  @Override
  public Object getSearchTerm()
  {
    return this.searchTerm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#getSearchFields()
   */
  @Override
  public Set<SearchFields<?, ?>> getSearchFields()
  {
    Set<SearchFields<?, ?>> fields = new LinkedHashSet<SearchFields<?, ?>>();
    fields.add(SpecimenSearchFields.BARCODE);
    fields.add(SpecimenSearchFields.ID);
    fields.add(SpecimenSearchFields.LABEL);
    fields.add(ParticipantSearchFields.MRN);
    return fields;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.ReportManager#setSearchField(edu.bcm
   * .dldcc.big.acquire.query.data.SearchFields)
   */
  @Override
  public void setSearchField(SearchFields<?, ?> fields)
  {
    this.field = fields;

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#getSearchField()
   */
  @Override
  public SearchFields<?, ?> getSearchField()
  {
    return this.field;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#runSearch()
   */
  @Override
  public List<SearchResult> runSearch() throws IdentityException,
      FeatureNotSupportedException
  {
    this.searchEngine.clearFields();
    SearchFields<?, ?> field = this.getSearchField();
    Map<SearchFields<?, ?>, SearchCriteria<?>> map;

    if (field instanceof ParticipantSearchFields)
    {
      this.searchEngine.addPatientSearchField(field);
      map = this.searchEngine.getPatientFieldValues();
    }
    else
    {
      this.searchEngine.addSpecimenSearchField(field);
      map = this.searchEngine.getSpecimenFieldValues();
    }

    map.get(field).setValue(this.getSearchTerm());

    processCollectionSiteForSearch(true);

    this.searchResults = searchEngine.runQuery();
    return new ArrayList<SearchResult>(this.searchResults);
  }

  private void processCollectionSiteForSearch(boolean onlyUserSites)
      throws IdentityException, FeatureNotSupportedException
  {
    if (this.getCollectionSite() != null && !this.getCollectionSite().isEmpty())
    {
      this.searchEngine
          .addSpecimenSearchField(SpecimenSearchFields.SPECIMEN_COLLECTION_SITE_NAME);
      List<String> values =
          searchEngine.getSpecimenFieldValues(
              SpecimenSearchFields.SPECIMEN_COLLECTION_SITE_NAME, String.class);
      values.add(this.getCollectionSite());
    }
    else if (onlyUserSites)
    {
      this.searchEngine
          .addSpecimenSearchField(SpecimenSearchFields.SPECIMEN_COLLECTION_SITE_NAME);
      List<String> values =
          searchEngine.getSpecimenFieldValues(
              SpecimenSearchFields.SPECIMEN_COLLECTION_SITE_NAME, String.class);
      values.addAll(this.permissions.getCurrentSites());

    }
  }

  /**
   * @return the searchResults
   */
  @Override
  public List<SearchResult> getSearchResults()
  {
    return this.searchResults;
  }

  /**
   * @return the notifications
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IllegalArgumentException
   * @throws SecurityException
   */
  public SearchResultDataModel getNotifications() throws IdentityException,
      FeatureNotSupportedException
  {
    return this.notifications;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.ReportManager#addNotification(edu.bcm
   * .dldcc.big.acquire.query.data.SearchResult)
   */
  @Override
  public void addNotification(SearchResult add) throws IdentityException
  {
    AttributesManager am = identitySession.getAttributesManager();
    am.addAttribute(identity.getUser(), ReportManagerImpl.ADD_ATTRIBUTE_NAME,
        add.getUuid());
    // this.notifications.add(add);

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.ReportManager#removeNotification(edu
   * .bcm.dldcc.big.acquire.query.data.SearchResult)
   */
  @Override
  public void removeNotification(SearchResult remove)
  {
    // not yet implemented

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#fetchNotifications()
   */
  @Override
  public void fetchNotifications() throws IdentityException,
      FeatureNotSupportedException
  {
    this.searchEngine.clearFields();
    searchEngine.addSpecimenSearchField(AnnotationSearchFields.STATUS);
    List<SpecimenStatus> status =
        searchEngine.getSpecimenFieldValues(AnnotationSearchFields.STATUS,
            SpecimenStatus.class);
    status.addAll(updates);
    Collection<String> added = this.getAddedNotifications();
    if (added != null && !added.isEmpty())
    {
      searchEngine.addSpecimenSearchField(AnnotationSearchFields.UUID);
      List<String> ids =
          searchEngine.getSpecimenFieldValues(AnnotationSearchFields.UUID,
              String.class);
      ids.addAll(added);
    }
    this.processCollectionSiteForSearch(true);
    List<SearchResult> results =
        new ArrayList<SearchResult>(searchEngine.runQuery());
    searchEngine.clearFields();
    /*
     * TODO: Add in code to handle remove notifications
     */
    this.notifications.setWrappedData(results);

  }

  private Collection<String> getAddedNotifications()
  {
    try
    {
      Collection<String> result = new ArrayList<String>();
      Attribute added =
          identitySession.getAttributesManager().getAttribute(
              identity.getUser(), ReportManagerImpl.ADD_ATTRIBUTE_NAME);
      if (added != null)
      {
        result = (Collection<String>) added.getValues();
      }
      return result;
    }
    catch (IdentityException e)
    {
      /*
       * If we can't get to the user, just leave out any user-added
       * notifications
       */
      return new ArrayList<String>();
    }
  }

  /**
   * @return the collectionSite
   */
  public String getCollectionSite()
  {
    return this.collectionSite;
  }

  /**
   * @param collectionSite
   *          the collectionSite to set
   */
  public void setCollectionSite(String collectionSite)
  {
    this.collectionSite = collectionSite;
  }

  /**
   * @return the overviewType
   */
  public Reports getReportType()
  {
    return this.reportType;
  }

  /**
   * @param overviewType
   *          the overviewType to set
   */
  public void setReportType(Reports overviewType)
  {
    this.reportType = overviewType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#getReportTypes()
   */
  @Override
  @Produces
  @ApplicationScoped
  @Named("reportTypes")
  public List<Reports> getReportTypes()
  {
    List<Reports> types = new ArrayList<Reports>();
    types.add(Reports.ALL);
    types.add(Reports.FULLY_QUALIFIED);
    types.add(Reports.SHIPPED_TCGA);
    types.add(Reports.POTENTIAL_QUALIFIED);
    types.add(Reports.QC_REPORT);
    types.add(Reports.NA_LAB_REPORT);
    return types;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#getQCReport()
   */
  @Override
  public List<SearchResult> getQcReport() throws IdentityException,
      SecurityException, FeatureNotSupportedException
  {
    return this.qcReport;
  }

  @Override
  public List<NaLabReportData> getNaLabReport() throws IdentityException,
      SecurityException, FeatureNotSupportedException
  {
    return this.naLabReport;
  }

  private void setupNaLabReport() throws IdentityException, SecurityException,
      FeatureNotSupportedException
  {
    List<SearchResult> aliquots = this.getQcReport();
    this.naLabReport.clear();

    for (SearchResult aliquot : aliquots)
    {
      EntityMap aliquotMap = this.em.find(EntityMap.class, aliquot.getUuid());
      EntityManager inventoryEm =
          this.caTissueEms.select(new OperationsLiteral(),
              new CaTissueLiteral(aliquotMap.getCaTissue())).get();
      CriteriaBuilder cb = inventoryEm.getCriteriaBuilder();
      CriteriaQuery<Specimen> criteria = cb.createQuery(Specimen.class);
      Root<Specimen> root = criteria.from(Specimen.class);
      criteria.select(root);
      ParameterExpression<String> labelParam =
          cb.parameter(String.class, "label");
      criteria.where(cb.equal(root.<String> get("label"), labelParam));
      AliquotAnnotation processed = aliquot.getAnnotation();
      SpecimenAnnotation specimen = aliquot.getsAnnotation();
      Participant participant = aliquot.getParticipant();
      Specimen aliquotSpecimen = aliquot.getSpecimen();
      AbstractSpecimen parent = aliquot.getParent();

      for (NaLabAnnotation naData : processed.getNaLabAnnotations())
      {
        NaLabReportData reportData = new NaLabReportData();
        reportData.setAliquot(processed);
        reportData.setAnnotation(specimen);
        reportData.setParticipant(participant);
        reportData.setParent(parent);
        reportData.setAliquotSpecimen(aliquotSpecimen);
        reportData.setNaLab(naData);
        TypedQuery<Specimen> query = inventoryEm.createQuery(criteria);
        query.setParameter(labelParam, naData.getNaLabel());
        reportData.setDerivative(query.getSingleResult());
        this.naLabReport.add(reportData);
      }

    }
  }

  @Override
  public void runQCReport() throws IdentityException, SecurityException,
      FeatureNotSupportedException
  {
    this.util.startConversation();
    Reports reportType = this.getReportType();

    /*
     * NA_LAB_REPORT is different, because it's pulled from a pre-run query
     */
    if (reportType == Reports.NA_LAB_REPORT)
    {
      /*
       * Get a copy of the full list
       */
      List<NaLabReportData> report =
          new ArrayList<NaLabReportData>(this.naLabReportBase);

      /*
       * If there's a collection site entered, we need to filter the list
       */
      if (this.collectionSite != null && !this.collectionSite.isEmpty())
      {
        /*
         * Compare the site of each row in the list to the user's selected site,
         * and remove those that don't match
         */
        for (NaLabReportData current : this.naLabReportBase)
        {
          if (!current.getAliquotSpecimen().getSpecimenCollectionGroup()
              .getSpecimenCollectionSite().getName()
              .equalsIgnoreCase(this.collectionSite))
          {
            report.remove(current);
          }
        }
      }

      this.naLabReport = report;
    }
    else
    {
      this.searchEngine.clearFields();

      if (reportType == Reports.QC_REPORT
          || reportType == Reports.NA_LAB_REPORT)
      {
        this.processCollectionSiteForSearch(false);
      }

      reportType.initReport(searchEngine);
      this.qcReport = new ArrayList<SearchResult>(this.searchEngine.runQuery());
      this.searchEngine.setIncludeAliquots(false);
      this.searchEngine.setIncludeNormals(false);
    }

  }

  @Override
  public void runPathologyReport() throws IdentityException,
      FeatureNotSupportedException
  {
    this.util.startConversation();
    this.searchEngine.setIncludeAliquots(false);
    List<SearchResult> results = this.runSearch();
    this.pathologyDataModel.setWrappedData(results);

    /*
     * If no specimen were found in the first report, we skip the second. This
     * avoids issues with searches that would only return aliquots
     */
    if (this.pathologyDataModel.getRowCount() > 0)
    {
      this.searchEngine.clearFields();
      this.searchEngine.addSpecimenSearchField(AliquotSearchFields.PARENT);
      List<SpecimenAnnotation> parent =
          searchEngine.getSpecimenFieldValues(AliquotSearchFields.PARENT,
              SpecimenAnnotation.class);
      for (SearchResult specimen : results)
      {
        parent.add(specimen.getsAnnotation());
      }
      this.searchEngine.setIncludeAliquots(true);
      this.aliquotDataModel = new SearchResultDataModel();
      Set<SearchResult> aliquotResults = new HashSet<SearchResult>(this.searchEngine.runQuery());
      aliquotResults.addAll(results);
      this.aliquotDataModel.addAll(aliquotResults);
      this.searchEngine.clearFields();
      this.searchEngine.setIncludeAliquots(false);
    }
    else
    {
      this.aliquotDataModel.clear();
    }
  }

  @Override
  public void runShipmentFormReport() throws IdentityException,
      FeatureNotSupportedException
  {
    this.util.startConversation();
    this.searchEngine.setIncludeNormals(true);
    this.searchEngine.setIncludeAliquots(true);
    this.shipmentDataModel.addAll(this.runSearch());
    /*
     * Exclude DNA/RNA from shipment report--
     */
    if (this.shipmentDataModel.getRowCount() > 0)
    {
      this.searchEngine.clearFields();
      this.searchEngine.addSpecimenSearchField(SpecimenSearchFields.TYPE);
      List<String> types =
          this.searchEngine.getSpecimenFieldValues(SpecimenSearchFields.TYPE,
              String.class);
      types.add("DNA");
      types.add("RNA");
    }
    this.searchEngine.setIncludeAliquots(false);
    this.searchEngine.setIncludeNormals(false);

  }

  @Override
  public SearchResultDataModel getPathologyReport()
  {
    return this.pathologyDataModel;
  }

  @Override
  public SearchResultDataModel getAliquotReport()
  {
    return this.aliquotDataModel;
  }

  @Override
  public List<SearchResult> getFilteredNotifications()
  {
    return this.filteredNotifications;
  }

  @Override
  public void
      setFilteredNotifications(List<SearchResult> filteredNotifications)
  {
    this.filteredNotifications = filteredNotifications;
  }

  @Override
  public void saveUpdates()
  {
    this.em.flush();
    this.util.endConversation();
    this.searchResults.clear();
    this.pathologyReport.clear();
    this.aliquotReport.clear();
    if (this.notifications != null)
    {
      this.notifications.clear();
    }
    if (this.filteredNotifications != null)
    {
      this.filteredNotifications.clear();
    }
    this.collectionSite = "";
    this.searchTerm = "";
    this.setReportType(Reports.ALL);

    messages.info("The updates were saved successfully.");
  }

  @Override
  public void applyUpdates()
  {
    this.em.flush();
    messages.info("The updates were saved successfully.");
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.ReportManager#getShipmentReport()
   */
  @Override
  public SearchResultDataModel getShipmentReport()
  {
    return this.shipmentDataModel;
  }

  @Override
  public void clearShipmentReport()
  {
    this.shipmentDataModel.clear();
  }

  @Override
  public void shipToTcga(SearchResult specimen)
  {
    specimen.shipToTcga();
    specimenEvent.fire(specimen);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.ReportManager#getFilteredNaLabReport()
   */
  @Override
  public List<NaLabReportData> getFilteredNaLabReport()
  {
    return this.filteredNaLabReport;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.inventory.ReportManager#setFilteredNaLabReport
   * (java.util.List)
   */
  @Override
  public void setFilteredNaLabReport(List<NaLabReportData> data)
  {
    this.filteredNaLabReport = data;
  }

}
