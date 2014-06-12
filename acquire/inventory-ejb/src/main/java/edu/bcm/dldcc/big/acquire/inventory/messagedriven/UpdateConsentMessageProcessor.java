package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import edu.bcm.dldcc.big.acquire.event.SpecimenUpdateEvent;
import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.Consent;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLabStatus;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.utility.entity.UpdateConsentLog;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.Specimen;

/**
 * Message-Driven Bean implementation class for: UpdateConsentMessage
 * 
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability",
        propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
        propertyValue = "Update Participant"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/updateConsent") },
    mappedName = "updateConsentTopic")
public class UpdateConsentMessageProcessor extends
    AbstractCaTissueEntityProcessor implements MessageListener
{

  @Inject
  private EntityResolver resolver;

  @Inject
  @Consent
  @NaLabStatus
  private Event<SpecimenUpdateEvent> consentEvent;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public UpdateConsentMessageProcessor()
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
    UpdateConsentLog log = (UpdateConsentLog) message.getObject();
    if (log.getRegistration() != null)
    {

      CollectionProtocolRegistration response =
          ctEm.merge(log.getRegistration());
      List<Specimen> specimens =
          this.resolver.findParticipantSpecimens(response.getParticipant());
      for (Specimen current : specimens)
      {
        SpecimenAnnotation annotation =
            this.resolver.getAnnotationForEntity(SpecimenAnnotation.class,
                current, instance, Specimen.class);
        if (annotation != null)
        {
          /*
           * Skip, as the specimen isn't processed yet, and these events will
           * be run when that happens any way
           */
          this.consentEvent.fire(new SpecimenUpdateEvent(current, annotation,
              instance));
        }

      }
    }

  }

}
