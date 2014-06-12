/**
 * 
 */
package edu.bcm.dldcc.big.annotation.listeners;

import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import edu.bcm.dldcc.big.acquire.qualifiers.AnnotationUpdate;
import edu.bcm.dldcc.big.acquire.qualifiers.NaLabStatus;
import edu.bcm.dldcc.big.acquire.qualifiers.Tcga;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;
import edu.bcm.dldcc.big.clinical.entity.SpecimenAnnotation;

/**
 * @author pew
 * 
 */
@Singleton
public class EntityCDIListenerBridge
{

  @Inject
  @NaLabStatus
  @Tcga
  private Event<SpecimenAnnotation> event;
  
  @Inject
  @AnnotationUpdate
  private MessageProducer updateProducer;

  @Inject
  private Session updateSession;

  /**
   * 
   */
  public EntityCDIListenerBridge()
  {
    super();
  }

  public void fireSpecimenAnnotationUpdateEventForAliquot(
      AliquotAnnotation annotation) throws JMSException
  {
    SpecimenAnnotation specimen = null;

    if (annotation.getParent() != null)
    {
      specimen = annotation.getParent();
    }
    else
    {
      specimen = annotation.getSpecimenFields();
    }
    
    this.event.fire(specimen);
    
    Message message = this.updateSession.createMessage();
    message.setJMSCorrelationID("Acquire");
    this.updateProducer.send(message);
    
  }

}
