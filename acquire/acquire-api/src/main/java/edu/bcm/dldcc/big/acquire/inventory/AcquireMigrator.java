package edu.bcm.dldcc.big.acquire.inventory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.jboss.seam.international.status.Messages;

import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.SuperAdmin;
import edu.bcm.dldcc.big.acquire.event.NaLabReportEvent;
import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AnnotationUpdate;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissue;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.NaLabSample;
import edu.bcm.dldcc.big.acquire.shipment.naLab.entity.NaLabSample_;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation_;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation_;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.inventory.entity.EntityMap_;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.wustl.catissuecore.domain.AbstractSpecimen;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCharacteristics;

/**
 * A utility class designed to hold various ad-hoc methods useful for super 
 * admins in correcting data, testing, and similar functions.
 */
@Singleton
@Named("migrator")
@LocalBean
@SuperAdmin
public class AcquireMigrator
{

  @Inject
  @Annotations
  @Admin
  private EntityManager em;

  @Inject
  @CaTissue(instance = CaTissueInstance.TCRB)
  @Admin
  private EntityManager caTissueEm;

  @Inject
  private EntityResolver resolver;

  @Inject
  private Messages messages;

  @Inject
  private Event<NaLabReportEvent> naLabEvent;

  @Inject
  @AnnotationUpdate
  private MessageProducer updateProducer;

  @Inject
  private Session updateSession;

  @Inject
  private CamelContext camelContext;

  /**
   * Default constructor.
   */
  public AcquireMigrator()
  {
    super();
  }

  /**
   * This method will find all caTissue Specimens without a listed 
   * tissue site, and set the value for that Specien to "Not Specified"
   */
  public void cleanNullSites()
  {
    CriteriaBuilder cb = caTissueEm.getCriteriaBuilder();
    CriteriaQuery<Specimen> criteria = cb.createQuery(Specimen.class);
    Root<Specimen> root = criteria.from(Specimen.class);
    criteria.select(root);
    criteria.where(cb.isNull(root.get("specimenCharacteristics")));
    List<Specimen> specimenList =
        caTissueEm.createQuery(criteria).getResultList();
    for (Specimen current : specimenList)
    {
      SpecimenCharacteristics sc = new SpecimenCharacteristics();
      sc.setTissueSite("Not Specified");
      current.setSpecimenCharacteristics(sc);
      caTissueEm.persist(sc);
    }

    this.messages.info("Finished fixing disease sites");
  }

  /**
   * This method will find any AliquotAnnotations that do not have parent 
   * information in Acquire and connect them to the proper 
   * SpecimenAnnotation object.
   */
  public void restoreParentSpecimen()
  {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Tuple> findCriteria = cb.createQuery(Tuple.class);
    Root<EntityMap> findRoot = findCriteria.from(EntityMap.class);
    Root<AliquotAnnotation> aliquotRoot =
        findCriteria.from(AliquotAnnotation.class);
    findCriteria.multiselect(
        findRoot.get(EntityMap_.entityId).alias("caTissueId"),
        aliquotRoot.alias("aliquot"));

    Subquery<String> specimen = findCriteria.subquery(String.class);
    Root<SpecimenAnnotation> specimenRoot =
        specimen.from(SpecimenAnnotation.class);
    specimen.select(specimenRoot.get(SpecimenAnnotation_.entityId));

    findCriteria.where(
        cb.and(cb.equal(findRoot.get(EntityMap_.id),
            aliquotRoot.get(AliquotAnnotation_.entityId))),
        cb.not(cb.in(aliquotRoot.get(AliquotAnnotation_.entityId)).value(
            specimen)), cb.isNull(aliquotRoot.get(AliquotAnnotation_.parent)));
    List<Tuple> aliquots = em.createQuery(findCriteria).getResultList();

    CriteriaQuery<SpecimenAnnotation> annotationCriteria =
        cb.createQuery(SpecimenAnnotation.class);
    Root<SpecimenAnnotation> annotationRoot =
        annotationCriteria.from(SpecimenAnnotation.class);
    annotationCriteria.select(annotationRoot);

    ParameterExpression<Long> link = cb.parameter(Long.class, "caTissueLink");

    Subquery<String> subquery = annotationCriteria.subquery(String.class);
    Root<EntityMap> subqueryRoot = subquery.from(EntityMap.class);
    subquery.select(subqueryRoot.get(EntityMap_.id));
    subquery.where(cb.and(
        cb.equal(subqueryRoot.get(EntityMap_.entityId), link),
        cb.equal(subqueryRoot.get(EntityMap_.entityName),
            Specimen.class.getName())));

    annotationCriteria.where(cb.equal(
        annotationRoot.get(SpecimenAnnotation_.entityId), subquery));

    for (Tuple result : aliquots)
    {
      Specimen caTissueSpecimen =
          caTissueEm.find(Specimen.class, result.get("caTissueId", Long.class));
      AbstractSpecimen parent = resolver.getAdam(caTissueSpecimen);
      TypedQuery<SpecimenAnnotation> query = em.createQuery(annotationCriteria);
      query.setParameter(link, parent.getId());
      result.get("aliquot", AliquotAnnotation.class).setParent(
          query.getSingleResult());

    }

    this.messages.info("Finished restoring parent specimen");
  }

  /**
   * This method will start a manual run of the NA Lab Report collection.
   */
  public void startNaLabReport()
  {
    this.naLabEvent.fire(new NaLabReportEvent());
    this.messages.info("Finished running NA Lab report");
  }

  /**
   * This method ensures that aliquots that were shipped to the NA Lab 
   * are correctly indicated in Acquire.
   */
  public void migrateShippedStatus()
  {
    /*
     * Get the specimen labels for all the specimen/aliquots that have been
     * added to a shipment form.
     */
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<String> criteria = cb.createQuery(String.class);
    Root<NaLabSample> root = criteria.from(NaLabSample.class);
    criteria.select(root.get(NaLabSample_.sampleId));
    List<String> labels = em.createQuery(criteria).getResultList();

    /*
     * Use the labels to get a list of caTissue specimenIds that correspond
     */
    CriteriaBuilder caTissueCb = caTissueEm.getCriteriaBuilder();
    CriteriaQuery<Long> specimenCriteria = caTissueCb.createQuery(Long.class);
    Root<Specimen> specimenRoot = specimenCriteria.from(Specimen.class);
    specimenCriteria.select(specimenRoot.<Long> get("id"));
    specimenCriteria.where(specimenRoot.<String> get("label").in(labels));
    List<Long> caTissueIds =
        caTissueEm.createQuery(specimenCriteria).getResultList();

    /*
     * Use the list of specimenIds to get the AliquotAnnotations that
     * correspond, using the EntityMap to make the connection
     */
    CriteriaQuery<AliquotAnnotation> aliquotCriteria =
        cb.createQuery(AliquotAnnotation.class);
    Root<AliquotAnnotation> aliquotRoot =
        aliquotCriteria.from(AliquotAnnotation.class);
    Root<EntityMap> mapRoot = aliquotCriteria.from(EntityMap.class);
    aliquotCriteria.select(aliquotRoot);

    /*
     * Start building the Predicate by matching AliquotAnnotation with EntityMap
     */
    Predicate where =
        cb.equal(mapRoot.get(EntityMap_.id),
            aliquotRoot.get(AliquotAnnotation_.entityId));

    /*
     * Restrict to the EntityMap instances that represent the specimen in our
     * list
     */
    Predicate mapWhere =
        cb.and(cb.equal(mapRoot.get(EntityMap_.entityName),
            "edu.wustl.catissuecore.domain.Specimen"), cb.equal(
            mapRoot.get(EntityMap_.caTissue), CaTissueInstance.TCRB), mapRoot
            .get(EntityMap_.entityId).in(caTissueIds));

    /*
     * Add the combined restriction to the criteria
     */
    aliquotCriteria.where(where, mapWhere);

    /*
     * Get the results
     */
    List<AliquotAnnotation> aliquots =
        em.createQuery(aliquotCriteria).getResultList();

    /*
     * Update the aliquots to mark them as shipped to NA Lab
     */
    for (AliquotAnnotation annotation : aliquots)
    {
      annotation.getStatus().add(SpecimenStatus.SHIPPED_NA_LAB);
    }

    this.messages.info("Shipped to NA Lab migrated to aliquots");
  }

  /**
   * Tests that messages trigger the scoreboard update properly.
   */
  public void testScoreboardMessage()
  {
    NotifyBuilder notify =
        new NotifyBuilder(this.camelContext)
            .from("sjms:queue:annotationUpdateQueue").whenDone(1).create();

    try
    {
      Message message = this.updateSession.createMessage();
      message.setJMSCorrelationID("Acquire");
      this.updateProducer.send(message);
      boolean done = notify.matches(100, TimeUnit.SECONDS);
      this.messages.info("Message done. Success = " + done);
    }
    catch (JMSException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /*public void testPasswordAuthentication()
  {
    DldccPasswordEncoder encoder = new DldccPasswordEncoder();
    CriteriaBuilder cb = this.adminEm.getCriteriaBuilder();
    CriteriaQuery<String> criteria = cb.createQuery(String.class);
    Root<IdentityObjectCredential> root =
        criteria.from(IdentityObjectCredential.class);
    criteria.select(root.get(IdentityObjectCredential_.value));
    criteria.where(cb.equal(root.get(IdentityObjectCredential_.identityObject)
        .get(IdentityObject_.name), "frodo@hobbiton.com"));
    String password = this.adminEm.createQuery(criteria).getSingleResult();
    String test = encoder.encode("Nazgul505");
    boolean check = password.equals(test);
    this.messages.info("Passwords match: " + check); 

  }
  */
  
  
}
