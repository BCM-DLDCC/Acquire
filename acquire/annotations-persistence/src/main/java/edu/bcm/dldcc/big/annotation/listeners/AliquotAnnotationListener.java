package edu.bcm.dldcc.big.annotation.listeners;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;
import javax.jms.JMSException;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.clinical.entity.AliquotAnnotation;

/**
 * 
 */
public class AliquotAnnotationListener
{

  /**
   * Default constructor.
   */
  public AliquotAnnotationListener()
  {
    super();
  }

  @PreUpdate
  @PrePersist
  public void aliquotUpdated(AliquotAnnotation annotation) throws JMSException
  {
    /*
     * Because EntityListeners are not CDI-enabled (until CDI 1.1), and Events
     * aren't able to be looked up, a separate bridge class is provided, that
     * can be looked up and is a CDI Bean, and thus can fire the event.
     */
    EntityCDIListenerBridge bridge = Resources.lookup(
        EntityCDIListenerBridge.class, new AnnotationLiteral<Default>()
        {
        });
    bridge.fireSpecimenAnnotationUpdateEventForAliquot(annotation);

  }

}
