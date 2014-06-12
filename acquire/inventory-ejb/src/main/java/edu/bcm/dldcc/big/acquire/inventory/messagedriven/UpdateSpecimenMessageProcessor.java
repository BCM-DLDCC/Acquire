package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.event.SpecimenUpdateEvent;
import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLabStatus;
import edu.bcm.dldcc.big.acquire.qualifiers.Tcga;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.utility.entity.UpdateSpecimenLog;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * Message-Driven Bean implementation class for: NewParticipantMessage
 * 
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability",
        propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
        propertyValue = "Update Specimen"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/updateSpecimen") },
    mappedName = "updateSpecimenTopic")
public class UpdateSpecimenMessageProcessor extends AbstractCaTissueEntityProcessor
    implements MessageListener
{

  @Inject
  private EntityResolver resolver;

  @Inject
  @NaLabStatus
  @Tcga
  private Event<SpecimenUpdateEvent> event;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public UpdateSpecimenMessageProcessor()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.inventory.messagedriven.
   * AbstractCaTissueEntityProcessor#processEntity(javax.jms.ObjectMessage,
   * javax.persistence.EntityManager,
   * edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance)
   */
  @Override
  protected void processEntity(ObjectMessage message, EntityManager ctEm,
      CaTissueInstance instance) throws JMSException
  {
    Specimen specimen = ((UpdateSpecimenLog) message.getObject()).getSpecimen();
    specimen = ctEm.merge(specimen);
    Specimen parent = (Specimen) this.resolver.getAdam(specimen);
    SpecimenAnnotation annotation = this.resolver.getAnnotationForEntity(
        SpecimenAnnotation.class, parent, instance, Specimen.class);

    this.event.fire(new SpecimenUpdateEvent(parent, annotation, instance));

  }

}
