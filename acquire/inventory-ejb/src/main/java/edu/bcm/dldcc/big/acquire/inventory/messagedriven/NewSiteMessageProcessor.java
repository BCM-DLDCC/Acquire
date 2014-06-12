package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;

import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.inventory.entity.EntityMap;
import edu.bcm.dldcc.big.inventory.entity.SiteAnnotation;
import edu.bcm.dldcc.big.utility.entity.NewSiteLog;
import edu.wustl.catissuecore.domain.Site;

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
        propertyValue = "New Site"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/newSite") }, mappedName = "newSiteTopic")
public class NewSiteMessageProcessor extends AbstractCaTissueEntityProcessor implements
    MessageListener
{

  @Inject
  private IdentitySession identity;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public NewSiteMessageProcessor()
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
    Site site = ((NewSiteLog) message.getObject()).getSite();
    if (site.getType().equals("Repository")
        || !site.getName().equals("In Transit"))
    {
      EntityMap map = this.addEntityToMap(site, instance, Site.class.getName());
      try
      {
        identity.getPersistenceManager().createGroup(site.getName(),
            Resources.TYPE_SITE);
      }
      catch (IdentityException e)
      {
        throw new IllegalStateException(e.getMessage(), e);
      }
      /*
       * Add corresponding entry in SiteAnnotation, to use in linking to
       * parent/child sites
       */
      SiteAnnotation annotation = new SiteAnnotation();
      annotation.setEntityId(map.getId());
      annotation.setName(site.getName());
      this.getAnnotationEntityManager().persist(annotation);
    }

  }

}
