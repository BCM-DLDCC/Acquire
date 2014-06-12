package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.acquire.event.MailExceptionEvent;
import edu.bcm.dldcc.big.acquire.event.NaLabReportEvent;
import edu.bcm.dldcc.big.acquire.listener.AcquireCDIListeners;
import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.query.Reports;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.NaLabReportData;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * Message-Driven Bean implementation class for: NewParticipantMessage
 * 
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "naLabReportQueue") }, mappedName = "naLabReportQueue")
public class NaLabMessageProcessor implements MessageListener
{

  @Inject
  private Event<NaLabReportEvent> event;
  
  @Inject
  @Admin 
  private SearchManager searchEngine;
  
  @Inject
  private Event<MailExceptionEvent> mailEvent;
  
  @Inject
  @Any 
  private Instance<EntityManager> entityManagers;
  
  @Inject
  @Admin 
  @Annotations 
  private EntityManager annotationEm;
  
  @Inject
  @Named("naLabReport")
  private List<NaLabReportData> report;

  /**
   * 
   */
  public NaLabMessageProcessor()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
   */
  @Override
  public void onMessage(Message message)
  {
    try
    {
      /*
       * Run the search to find the necessary Specimen, and reset the
       * SearchEngine for future use
       */
      System.out.println("Starting NA Lab report");
      Reports.NA_LAB_REPORT.initReport(searchEngine);
      System.out.println("Running the query");
      List<SearchResult> results =
          new ArrayList<SearchResult>(searchEngine.runQuery());
      searchEngine.setIncludeAliquots(false);
      searchEngine.setIncludeNormals(false);

      /*
       * Clear any existing report data
       */
      this.report.clear();

      /*
       * Process each result from the search to setup the NA Lab Report unique
       * data
       */
      for (SearchResult aliquot : results)
      {
        
        /*
         * Pull data from the SearchResult object that will be needed for the
         * NaLabReportData
         */
        AliquotAnnotation processed = aliquot.getAnnotation();
        SpecimenAnnotation specimen = aliquot.getsAnnotation();
        Participant participant = aliquot.getParticipant();
        Specimen aliquotSpecimen = aliquot.getSpecimen();
        AbstractSpecimen parent = aliquot.getParent();
        
        /*
         * Get all the children of this specimen and put them in a map, with
         * the Label as the key
         */
        Map<String, AbstractSpecimen> childMap = new HashMap<String, AbstractSpecimen>();        
        for(AbstractSpecimen child : aliquotSpecimen.getChildSpecimenCollection())
        {
          childMap.put(child.getLabel(), child);
        }

        /*
         * Loop through the NaLabAnnotations for the specimen
         */
        for (NaLabAnnotation naData : processed.getNaLabAnnotations())
        {
          /*
           * Create an NaLabReportData instance and populate basic information
           */
          NaLabReportData reportData = new NaLabReportData();
          reportData.setAliquot(processed);
          reportData.setAnnotation(specimen);
          reportData.setParticipant(participant);
          reportData.setParent(parent);
          reportData.setAliquotSpecimen(aliquotSpecimen);
          reportData.setNaLab(naData);
          
          /*
           * Get the derivative specimen information out of the map, using the
           * label stored in the AnLabAnnotation
           */
          reportData.setDerivative((Specimen) childMap.get(naData.getNaLabel())); 
          
          /*
           * add the NaLabReportData to the report list
           */
          this.report.add(reportData);
        }

      }
    }
    catch (RuntimeException e)
    {
      /*
       * If there is an exception, fire a MailExceptionEvent
       */
      mailEvent.fire(new MailExceptionEvent("Error running NA Lab Report on ",
          e));
      throw e;
    }
    catch (Throwable e)
    {
      /*
       * fire MailExceptionEvent, but don't rethrow...
       */
      mailEvent.fire(new MailExceptionEvent("Error running NA Lab report on ",
          e));
    }


  }

}
