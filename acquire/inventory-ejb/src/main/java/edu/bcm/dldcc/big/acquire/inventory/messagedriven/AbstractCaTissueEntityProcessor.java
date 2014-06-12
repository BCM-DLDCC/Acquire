/**
 * 
 */
package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;

import edu.bcm.dldcc.big.acquire.qualifiers.Admin;
import edu.bcm.dldcc.big.acquire.qualifiers.AdminLiteral;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueLiteral;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.wustl.common.domain.AbstractDomainObject;

/**
 * @author pew
 * 
 */
abstract public class AbstractCaTissueEntityProcessor implements MessageListener
{
  @Admin
  @Annotations
  @Inject
  private EntityManager annotationEntityManager;
  
  @Any
  @Inject
  private Instance<EntityManager> caTissueEntityManager;

  /**
   * 
   */
  public AbstractCaTissueEntityProcessor()
  {
    super();
  }

  protected EntityMap addEntityToMap(AbstractDomainObject object,
      CaTissueInstance instance, String className)
  {

    EntityMap map = new EntityMap();
    map.setEntityId(object.getId());
    map.setCaTissue(instance);
    map.setEntityName(className);
    annotationEntityManager.persist(map);

    return map;
  }

  protected EntityManager getAnnotationEntityManager()
  {
    return this.annotationEntityManager;
  }

  @Override
  public void onMessage(Message message)
  {
    System.out.println("Message received in " + this.getClass().getSimpleName());
    try
    {
      CaTissueInstance instance = CaTissueInstance.valueOf(message
          .getStringProperty("instance"));
      EntityManager em = this.caTissueEntityManager.select(new AdminLiteral(),
          new CaTissueLiteral(instance)).get();
      EntityManager acquireEm = this.getAnnotationEntityManager();
      Session session = (Session) acquireEm.unwrap(Session.class);
      session.setFlushMode(FlushMode.MANUAL);
      this.processEntity((ObjectMessage) message,
          em, instance);
      acquireEm.flush();
    }
    catch (JMSException e)
    {
      /*
       * Wrap with an unchecked exception
       */
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  abstract protected void processEntity(ObjectMessage message, EntityManager ctEm,
      CaTissueInstance instance) throws JMSException;

  /**
   * @return the caTissueEntityManager
   */
  protected Instance<EntityManager> getCaTissueEntityManager()
  {
    return this.caTissueEntityManager;
  }

}
