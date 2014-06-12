package edu.bcm.dldcc.big.acquire.listener;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.event.PreRenderViewEvent;
import javax.inject.Named;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.jboss.seam.faces.event.qualifier.View;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.LoggedInEvent;
import org.jboss.seam.security.events.LoginFailedEvent;
import org.jboss.seam.security.events.NotAuthorizedEvent;
import org.jboss.weld.context.http.HttpConversationContext;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.admin.UserManager;
import edu.bcm.dldcc.big.acquire.event.MailExceptionEvent;
import edu.bcm.dldcc.big.acquire.event.NaLabReportEvent;
import edu.bcm.dldcc.big.acquire.event.SpecimenUpdateEvent;
import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Consent;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLabStatus;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.qualifiers.Tcga;
import edu.bcm.dldcc.big.acquire.query.Reports;
import edu.bcm.dldcc.big.acquire.query.SearchManager;
import edu.bcm.dldcc.big.acquire.query.data.AnnotationSearchFields;
import edu.bcm.dldcc.big.acquire.query.data.NaLabReportData;
import edu.bcm.dldcc.big.acquire.query.data.SearchResult;
import edu.bcm.dldcc.big.acquire.security.Authorizations;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.NaLabAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.search.SearchCriteria;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.bcm.dldcc.big.util.qualifier.Current;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ConsentTierResponse;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;

/**
 * Session Bean implementation class AcquireCDIListeners. This is a class
 * that consolidates many of the event observer methods.
 */
@Stateless
@LocalBean
public class AcquireCDIListeners
{

  @Produces
  @Named("naLabReport")
  private static List<NaLabReportData> report =
      new ArrayList<NaLabReportData>();

  /**
   * Default constructor.
   */
  public AcquireCDIListeners()
  {
    super();
  }

  /**
   * View event listener for the announcements page.
   * 
   * @param event
   * @param auth
   * @param program
   * @param util
   * @throws IdentityException
   */
  public void announcements(
      @Observes @View("/announcements.xhtml") PreRenderViewEvent event,
      Authorizations auth, @Named("currentProgram") String program,
      Utilities util) throws IdentityException
  {
    auth.isAnnouncements(program);
    util.startConversation();
  }

  /**
   * View event listener for the site management page.
   * 
   * @param event
   * @param auth
   * @param fail
   * @param util
   * @throws IdentityException
   */
  public void siteManager(
      @Observes @View("/siteManagement.xhtml") PreRenderViewEvent event,
      Authorizations auth, Event<NotAuthorizedEvent> fail, Utilities util)
      throws IdentityException
  {
    checkSuperAdmin(auth, fail);
    util.startConversation();
  }

  /**
   * Method to check if user is a super admin.
   * 
   * @param auth
   * @param fail
   * @throws IdentityException
   */
  private void checkSuperAdmin(Authorizations auth,
      Event<NotAuthorizedEvent> fail) throws IdentityException
  {
    if (!auth.isSuperAdmin())
    {
      NotAuthorizedEvent failEvent = new NotAuthorizedEvent();
      fail.fire(failEvent);
    }
  }

  /**
   * View event listener for the shipmentSearch page.
   * 
   * @param event
   * @param auth
   * @param program
   * @param util
   * @throws IdentityException
   */
  public void shipmentSearch(
      @Observes @View("/shipmentSearch.xhtml") PreRenderViewEvent event,
      Authorizations auth, @Named("currentProgram") String program,
      Utilities util)
      throws IdentityException
  {
    auth.isShipment(program);
    util.startConversation();
  }

  /**
   * View event listener for the naLabShipmentForm page.
   * 
   * @param event
   * @param auth
   * @param program
   * @throws IdentityException
   */
  public void naLabShipment(
      @Observes @View("/naLabShipmentForm.xhtml") PreRenderViewEvent event,
      Authorizations auth, @Named("currentProgram") String program)
      throws IdentityException
  {
    auth.isShipment(program);
  }

  /**
   * View event listener for the super admin utility page
   * @param event
   * @param auth
   * @param fail
   * @throws IdentityException
   */
  public void migrator(
      @Observes @View("/migrator.xhtml") PreRenderViewEvent event,
      Authorizations auth, Event<NotAuthorizedEvent> fail)
      throws IdentityException
  {
    this.checkSuperAdmin(auth, fail);
  }

  /**
   * View event listener for the search page.
   * 
   * @param event
   * @param util
   */
  public void searching(
      @Observes @View("/miner.xhtml") PreRenderViewEvent event, Utilities util)
  {
    util.startConversation();
  }

  /**
   * View event listener for the application page.
   * @param event
   * @param util
   */
  public void racApply(
      @Observes @View("/apply.xhtml") PreRenderViewEvent event, Utilities util)
  {
    util.startConversation();
  }

  /**
   * View event listener for the home page.
   * @param event
   * @param auth
   * @param context
   * @throws IdentityException
   * @throws IOException
   */
  public void index(@Observes @View("/index.xhtml") PreRenderViewEvent event,
      Authorizations auth, ExternalContext context) throws IdentityException,
      IOException
  {
    if (auth.isPublicUser())
    {
      context.redirect(context.getRequestContextPath() + "/miner.jsf");
    }
  }

  /**
   * View event listener for the change password page.
   * 
   * @param event
   * @param currentUser
   * @param fail
   */
  public void changePassword(
      @Observes @View("/changePassword.xhtml") PreRenderViewEvent event,
      @Current AcquireUserInformation currentUser,
      Event<NotAuthorizedEvent> fail)
  {
    if (currentUser.getCaTissueUser())
    {
      NotAuthorizedEvent failEvent = new NotAuthorizedEvent();
      fail.fire(failEvent);
    }
  }

  /**
   * View event listener for the user management page.
   * 
   * @param event
   * @param auth
   * @param program
   * @param util
   * @throws IdentityException
   */
  public void permissions(
      @Observes @View("/manageUsers.xhtml") PreRenderViewEvent event,
      Authorizations auth, @Named("currentProgram") String program,
      Utilities util) throws IdentityException
  {
    auth.isAdmin(program);
    util.startConversation();
  }

  /**
   * View event listener for the account request review page.
   * 
   * @param event
   * @param auth
   * @param program
   * @param util
   * @throws IdentityException
   */
  public void reviewUsers(
      @Observes @View("/reviewUsers.xhtml") PreRenderViewEvent event,
      Authorizations auth, @Named("currentProgram") String program,
      Utilities util) throws IdentityException
  {
    auth.isAdmin(program);
    util.startConversation();
  }

  /**
   * View event listener for the account request page.
   * 
   * @param event
   * @param util
   * @param userManager
   */
  public void requestAccount(
      @Observes @View("/public/requestAccount.xhtml") PreRenderViewEvent event,
      Utilities util, UserManager userManager)
  {
    util.startConversation();
  }

  /**
   * Listener for specimen updates to check and update the current consent 
   * status of the specimen.
   * 
   * @param event
   * @param mailEvent
   */
  public void determineConsentStatus(
      @Observes @Consent SpecimenUpdateEvent event,
      Event<MailExceptionEvent> mailEvent)
  {
    try
    {
      Specimen specimen = event.getSpecimen();
      SpecimenAnnotation annotation = event.getAnnotation();
      boolean consentGiven = false;
      SpecimenCollectionGroup scg = specimen.getSpecimenCollectionGroup();
      CollectionProtocolRegistration registration = null;
      if (scg == null)
      {
        System.out.println("No specimen collection group: "
            + specimen.getLabel());
      }
      else
      {
        registration = scg.getCollectionProtocolRegistration();
      }

      if (registration == null)
      {
        System.out.println("No collection protocol registration: "
            + specimen.getLabel());
      }
      else
      {
        if (registration.getConsentTierResponseCollection() != null)
        {
          for (ConsentTierResponse response : registration
              .getConsentTierResponseCollection())
          {
            if (response.getResponse() != null
                && response.getResponse().equalsIgnoreCase("yes"))
            {
              consentGiven = true;
              break;
            }
          }
        }
        else
        {
          System.out.println("No consent tier responses: "
              + specimen.getLabel());
        }
      }

      if (consentGiven)
      {
        annotation.getStatus().remove(SpecimenStatus.CONSENT);
      }
      else
      {
        annotation.getStatus().add(SpecimenStatus.CONSENT);
      }
    }
    catch (RuntimeException e)
    {
      mailEvent.fire(new MailExceptionEvent(
          "Error checking Consent Status on ", e));
      throw e;
    }
  }

  /**
   * Listener for specimen updates to ensure that the TCGA Qualified status
   * of the specimen is checked and updated, if necessary.
   * 
   * @param event
   * @param searchEngine
   * @param mailEvent
   */
  public void determineTCGAStatus(@Observes @Tcga SpecimenUpdateEvent event,
      @Admin SearchManager searchEngine, Event<MailExceptionEvent> mailEvent)
  {
    try
    {
      SpecimenAnnotation annotation = event.getAnnotation();
      searchEngine.clearFields();
      Reports.FULLY_QUALIFIED.initReport(searchEngine);
      searchEngine.addSpecimenSearchField(AnnotationSearchFields.UUID);
      List<String> searchValues =
          searchEngine.getSpecimenFieldValues(AnnotationSearchFields.UUID,
              String.class);
      SearchCriteria<?> search =
          searchEngine.getSpecimenFieldValues()
              .get(AnnotationSearchFields.UUID);
      search.setNewOperator(SearchOperator.EQ);
      search.addOperator();
      searchValues.add(annotation.getEntityId());
      long count = searchEngine.runCount();
      if (count == 1)
      {
        annotation.getStatus().add(SpecimenStatus.TCGA_QUALIFIED);
      }
      else
      {
        annotation.getStatus().remove(SpecimenStatus.TCGA_QUALIFIED);
      }
    }
    catch (RuntimeException e)
    {
      mailEvent
          .fire(new MailExceptionEvent("Error checking TCGA Status on ", e));
      throw e;
    }
  }

  /**
   * Listens to SpecimenAnnotation events and adapts them to a 
   * SpecimenUpdateEvent.
   * 
   * @param event
   * @param resolver
   * @param target
   */
  public void adaptSpecimenAnnotationEvent(
      @Observes @Tcga @NaLabStatus SpecimenAnnotation event,
      EntityResolver resolver,
      @Tcga @NaLabStatus Event<SpecimenUpdateEvent> target)
  {
    target.fire(new SpecimenUpdateEvent(resolver.getEntityForAnnotation(
        Specimen.class, event), event, resolver
        .getInstanceFromAnnotation(event)));
  }

  /**
   * SpecimenUpdateEvent listener to check and update NA Lab Status.
   * 
   * @param event
   * @param mailEvent
   */
  public void determineNaLabStatus(
      @Observes @NaLabStatus SpecimenUpdateEvent event,
      Event<MailExceptionEvent> mailEvent)
  {
    try
    {
      Specimen specimen = event.getSpecimen();
      SpecimenAnnotation annotation = event.getAnnotation();
      boolean hgsc = false;
      if (!annotation.getStatus().contains(SpecimenStatus.CONSENT)
          && !annotation.getStatus().contains(SpecimenStatus.SHIPPED_NA_LAB)
          && (specimen.getInitialQuantity() != null && specimen
              .getInitialQuantity() >= 25))
      {
        AliquotAnnotation aliquotData = annotation.getAliquotFields();
        if (aliquotData.getPercentNecrosis() != null
            && aliquotData.getPercentNecrosis() < 20
            && aliquotData.getPercentNuclei() != null
            && aliquotData.getPercentNuclei() > 60)
        {
          hgsc = true;
        }
        else
        {
          for (AliquotAnnotation aliquot : annotation.getAliquots())
          {
            if (aliquot.getPercentNecrosis() != null
                && aliquot.getPercentNecrosis() < 20
                && aliquot.getPercentNuclei() != null
                && aliquot.getPercentNuclei() > 60)
            {
              hgsc = true;
              break;
            }
          }
        }
      }

      if (hgsc)
      {
        annotation.getStatus().add(SpecimenStatus.NA_LAB_QUALIFIED);
      }
      else
      {
        annotation.getStatus().remove(SpecimenStatus.NA_LAB_QUALIFIED);
      }
    }
    catch (RuntimeException e)
    {
      mailEvent.fire(new MailExceptionEvent("Error checking NA Lab Status on ",
          e));
      throw e;
    }
  }

  /**
   * Observer that ensures the conversation timeout is set as desired when a
   * user logs in
   * 
   * @param event
   * @param context
   */
  public void conversationTimeout(@Observes LoggedInEvent event,
      HttpConversationContext context)
  {
    context.setDefaultTimeout(1200000L);
  }

  /**
   * Observer that redirects to the appropriate page if a user is not logged in.
   * 
   * @param event
   * @param context
   * @throws IOException
   */
  public void loginFailed(@Observes LoginFailedEvent event,
      ExternalContext context) throws IOException
  {
 
    context.redirect(context.getRequestContextPath() + 
        "/public/requestAccount.jsf?loginFailed=true");
  }

  /**
   * Observer that will redirect to the appropriate page if a user attempts an
   * operation that they do not have permission for.
   * 
   * @param event
   * @param context
   * @throws IOException
   */
  public void unauthorized(@Observes NotAuthorizedEvent event,
      ExternalContext context) throws IOException
  {

    context.redirect(context.getRequestContextPath() + "/denied.jsf");
  }

  /**
   * Observer for a SearchResult event that synchronizes the internal Annotation
   * objects of the SearchResult with the database.
   * 
   * @param result
   * @param em
   */
  public void saveSpecimen(@Observes SearchResult result,
      @Operations @Annotations EntityManager em)
  {
    em.merge(result.getAnnotation());
    em.merge(result.getsAnnotation());
    em.flush();
  }

  /**
   * CDI Event Listener that observes NaLabReportEvents, runs the search for the
   * NA Lab Report, and updates the cached results
   * 
   * 
   * @param event
   *          The event that triggers the operation
   * @param searchEngine
   *          A non-conversation scoped SearchManager to use in the search
   * @param mailEvent
   *          Event to be fired if an exception occurs
   */
  public void runNaLabReport(@Observes NaLabReportEvent event,
      @Admin SearchManager searchEngine, Event<MailExceptionEvent> mailEvent,
      @Any Instance<EntityManager> entityManagers,
      @Admin @Annotations EntityManager annotationEm)
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
      System.out.println("Clearing the list in memory");
      AcquireCDIListeners.report.clear();

      /*
       * Process each result from the search to setup the NA Lab Report unique
       * data
       */
      System.out.println("Looping through results to get NA Lab data");
      System.out.println("Search returned " + results.size() + " results");
      for (SearchResult aliquot : results)
      {
        /*
         * Get the EntityMap corresponding to the SearchResult
         */
        EntityMap aliquotMap =
            annotationEm.find(EntityMap.class, aliquot.getUuid());

        /*
         * Build the query to get the Derivative's specimen information from
         * caTissue
         */
        EntityManager inventoryEm =
            entityManagers.select(new AdminLiteral(),
                new CaTissueLiteral(aliquotMap.getCaTissue())).get();
        CriteriaBuilder cb = inventoryEm.getCriteriaBuilder();
        CriteriaQuery<Specimen> criteria = cb.createQuery(Specimen.class);
        Root<Specimen> root = criteria.from(Specimen.class);
        criteria.select(root);
        ParameterExpression<String> labelParam =
            cb.parameter(String.class, "label");
        criteria.where(cb.equal(root.<String> get("label"), labelParam));
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
         * Get all the children of this specimen and put them in a map, with the
         * Label as the key
         */
        Map<String, AbstractSpecimen> childMap =
            new HashMap<String, AbstractSpecimen>();
        for (AbstractSpecimen child : aliquotSpecimen
            .getChildSpecimenCollection())
        {
          childMap.put(child.getLabel(), child);
        }

        /*
         * Loop through the NaLabAnnotations for the specimen
         */
        System.out.println("Getting NA Lab rows for this specimen");
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
          reportData
              .setDerivative((Specimen) childMap.get(naData.getNaLabel()));

          /*
           * add the NaLabReportData to the report list
           */
          System.out.println("Adding a row to the list");
          AcquireCDIListeners.report.add(reportData);
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

  /**
   * Observer method that will mail the support team to report an exception
   * 
   * @param event
   *          Event that triggers the operation and provides some necessary
   *          information
   * @param mail
   *          Instance of MailMessage to be used to construct the email
   * @param support
   *          The address to send the email
   * @param server
   *          The server (environment) where the error occurred.
   */
  public void mailException(@Observes MailExceptionEvent event,
      Instance<MailMessage> mail, InternetAddress support,
      @Named("serverName") String server)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(event.getHeader());
    sb.append(server + ".\n\n ");
    StringWriter stackTrace = new StringWriter();
    event.getCause().printStackTrace(new PrintWriter(stackTrace));
    sb.append(stackTrace.toString());

    mail.get().from("Benjamin Pew<pew@bcm.edu>").to(support)
        .subject("Error with Acquire: " + event.getCause().getMessage())
        .bodyText(sb.toString()).send();
  }

}
