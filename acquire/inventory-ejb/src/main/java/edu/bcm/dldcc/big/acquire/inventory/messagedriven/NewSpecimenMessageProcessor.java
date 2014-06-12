package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import edu.bcm.dldcc.big.acquire.event.SpecimenUpdateEvent;
import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.Consent;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLabStatus;
import edu.bcm.dldcc.big.acquire.qualifiers.Tcga;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.ParticipantAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation_;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.inventory.entity.EntityMap_;
import edu.bcm.dldcc.big.utility.entity.NewSpecimenLog;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ExternalIdentifier;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.SpecimenCollectionGroup;

/**
 * Message-Driven Bean implementation class for: NewSpecimenMessage
 * 
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability",
        propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
        propertyValue = "New Specimen"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/newSpecimen") }, mappedName = "newSpecimenTopic")
public class NewSpecimenMessageProcessor extends
    AbstractCaTissueEntityProcessor implements MessageListener
{

  @Inject
  private EntityResolver resolver;

  @Inject
  private Utilities util;

  @Inject
  @NaLabStatus
  @Tcga
  @Consent
  private Event<SpecimenUpdateEvent> event;

  /**
   * Default constructor.
   */
  public NewSpecimenMessageProcessor()
  {
    super();
  }

  /**
   * Process Specimen entities. Create an Acquire UUID, and link it to the
   * Specimen. Also create a SpecimenAnnotation if not a derivative or an
   * aliquot, and an AliquotAnnotation if not a derivative
   * 
   */
  @TransactionAttribute
  @Override
  protected void processEntity(ObjectMessage message, EntityManager em,
      CaTissueInstance instance) throws JMSException
  {
    Specimen specimen = ((NewSpecimenLog) message.getObject()).getSpecimen();
    EntityManager acquireEm = this.getAnnotationEntityManager();
    specimen = em.merge(specimen);
    /*
     * Create an EntityMap object for each specimen to provide it with an
     * Acquire UUID
     */
    EntityMap map = this.addEntityToMap(specimen, instance,
        Specimen.class.getName());

    /*
     * Find the short title of the collection protocol, and set the appropriate
     * site in the specimen collection group
     */
    SpecimenCollectionGroup scg = specimen.getSpecimenCollectionGroup();

    String siteName = scg.getCollectionProtocolRegistration()
        .getCollectionProtocol().getShortTitle();
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Site> criteria = cb.createQuery(Site.class);
    Root<Site> root = criteria.from(Site.class);
    criteria.select(root);
    criteria.where(cb.equal(root.get("name"), siteName));
    Site collectionSite;
    try
    {
      collectionSite = em.createQuery(criteria).getSingleResult();
      scg.setSpecimenCollectionSite(collectionSite);
    }
    catch (NoResultException e)
    {
      /*
       * Remove the EntityMap for this specimen, and skip further processing
       * This specimen is not to be included in Acquire
       */
      acquireEm.remove(map);
      return;
    }

    /*
     * Create an ExternalIdentifier to store the Acquire UUID within caTissue
     * for convenience
     */
    ExternalIdentifier exId = new ExternalIdentifier();
    exId.setName(Specimen.ACQUIRE_EXTERNAL_ID_NAME);
    exId.setValue(map.getId());
    exId.setSpecimen(specimen);
    em.persist(exId);

    /*
     * Create the appropriate Annotation objects within Acquire. To do this we
     * need to know the type of the Specimen, which is unfortunately stored as a
     * String
     */
    String lineage = specimen.getLineage();

    /*
     * New specimen, derivatives and aliquots all need an AliquotAnnotation
     */

    /*
     * Create the instance, assign it the Acquire UUID for the specimen, and
     * persist it
     */
    AliquotAnnotation aliquot = new AliquotAnnotation();
    aliquot.setEntityId(map.getId());
    aliquot.setMap(map);
    
    SpecimenAnnotation annotation = null;
    /*
     * New Specimen need a SpecimenAnnotation
     */
    if (lineage.equals("New"))
    {
      /*
       * Create the instance, assign it the Acquire UUID for the specimen
       */ 
      annotation = new SpecimenAnnotation();
      annotation.setEntityId(map.getId());
      annotation.setAgeAtCollection(util.calculateAgeAtCollection(specimen,
          resolver.findParticipantForSpecimen(specimen)));
      annotation.setAliquotFields(aliquot);
      annotation.setCreateDate(new Date());
      this.associateWithParticipant(specimen, annotation, instance);

    }
    else
    {
      CriteriaBuilder annotationCb = acquireEm.getCriteriaBuilder();
      CriteriaQuery<SpecimenAnnotation> annotationCriteria = annotationCb
          .createQuery(SpecimenAnnotation.class);
      Root<SpecimenAnnotation> annotationRoot = annotationCriteria
          .from(SpecimenAnnotation.class);
      Root<EntityMap> entityMap = annotationCriteria.from(EntityMap.class);
      annotationCriteria.select(annotationRoot);
      annotationCriteria.where(annotationCb.and(annotationCb.equal(
          entityMap.get(EntityMap_.id),
          annotationRoot.get(SpecimenAnnotation_.entityId)), annotationCb.equal(
          entityMap.get(EntityMap_.entityId), resolver.getAdam(specimen)
              .getId())));
      TypedQuery<SpecimenAnnotation> query = acquireEm
          .createQuery(annotationCriteria);
      SpecimenAnnotation parent = query.getSingleResult();
      parent.addAliquot(aliquot);
    }
    
    acquireEm.persist(aliquot);
    if(annotation != null)
    {
      acquireEm.persist(annotation);
      this.event.fire(new SpecimenUpdateEvent(specimen, annotation, instance));
    }

  }

  private void associateWithParticipant(Specimen specimen,
      SpecimenAnnotation annotation, CaTissueInstance instance)
  {
    EntityManager acquireEm = this.getAnnotationEntityManager();
    Participant participant = resolver.findParticipantForSpecimen(specimen);
    CriteriaBuilder cb = acquireEm.getCriteriaBuilder();
    CriteriaQuery<EntityMap> criteria = cb.createQuery(EntityMap.class);
    Root<EntityMap> root = criteria.from(EntityMap.class);
    criteria.select(root);
    criteria.where(
        cb.equal(root.get(EntityMap_.entityId), participant.getId()),
        cb.equal(root.get(EntityMap_.caTissue), instance),
        cb.equal(root.get(EntityMap_.entityName), Participant.class.getName()));
    EntityMap participantMap = acquireEm.createQuery(criteria)
        .getSingleResult();
    ParticipantAnnotation acquireParticipant = acquireEm.find(
        ParticipantAnnotation.class, participantMap.getId());
    annotation.setPatient(acquireParticipant);
    for (CollectionProtocolRegistration registration : participant
        .getCollectionProtocolRegistrationCollection())
    {
      for (SpecimenCollectionGroup scg : registration
          .getSpecimenCollectionGroupCollection())
      {
        if (scg.getSpecimenCollection().contains(specimen))
        {
          if (scg.getCollectionProtocolEvent().getCollectionPointLabel()
              .toLowerCase().contains("normal"))
          {
            annotation.setNormal(true);
          }
        }
      }
    }
  }

}
