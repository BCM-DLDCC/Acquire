package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;

import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.util.SpecimenStatus;
import edu.bcm.dldcc.big.acquire.util.YesNoChoices;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;
import edu.bcm.dldcc.big.utility.entity.DynamicExtensionUpdateLog;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.domain.deintegration.SpecimenRecordEntry;

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
        propertyValue = "Dynamic Extension"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/dynamicExtension") },
    mappedName = "dynamicExtensionTopic")
public class DynamicExtensionMessageProcessor extends
    AbstractCaTissueEntityProcessor implements MessageListener
{

  @Inject
  private EntityResolver resolver;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public DynamicExtensionMessageProcessor()
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
    DynamicExtensionUpdateLog update =
        (DynamicExtensionUpdateLog) message.getObject();
    if (update.getSpecimenRecordEntry() != null)
    {
      SpecimenRecordEntry entry = update.getSpecimenRecordEntry();
      Specimen specimen = entry.getSpecimen();
      if (specimen.getLineage().equals("New"))
      {

        SpecimenAnnotation annotation =
            this.resolver.getAnnotationForEntity(SpecimenAnnotation.class,
                specimen, instance, Specimen.class);

        this.updateDynamicExtensionFields(annotation, specimen, update,
            instance);
      }
    }

  }

  private void updateDynamicExtensionFields(SpecimenAnnotation annotation,
      Specimen specimen, DynamicExtensionUpdateLog update,
      CaTissueInstance instance)
  {

    if ((update.getPriorTreatment() == null || update.getPriorTreatment()
        .equals("")) && (update.getWarmIschemia() == null))
    {
      // No entry exists for this specimen. Make sure values are null
      annotation.setPriorTreatment(null);
      annotation.setWarmIschemiaTime(null);
      annotation.getStatus().add(SpecimenStatus.CONDITION);
    }
    else
    {
      annotation.setWarmIschemiaTime(update.getWarmIschemia());
      String ptString = update.getPriorTreatment();
      ptString = ptString.replaceAll("\\s", "_");
      annotation
          .setPriorTreatment(YesNoChoices.valueOf(ptString.toUpperCase()));
    }
  }
}
