package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.picketlink.idm.api.IdentitySession;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation_;
import edu.bcm.dldcc.big.util.iso21090.model.EmailAddress;
import edu.bcm.dldcc.big.util.iso21090.model.UsAddress;
import edu.bcm.dldcc.big.util.values.UsStates;
import edu.bcm.dldcc.big.utility.entity.UpdateUserLog;
import edu.wustl.catissuecore.domain.User;

/**
 * Message-Driven Bean implementation class for: UpdateUserMessage
 * 
 */
@MessageDriven(activationConfig =
{
    @ActivationConfigProperty(propertyName = "destinationType",
        propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability",
        propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
        propertyValue = "Update User"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/userChange") }, mappedName = "userChangeTopic")
public class UpdateUserMessageProcessor extends AbstractCaTissueEntityProcessor
    implements MessageListener
{

  @Inject
  private IdentitySession identity;

  @Inject
  private EntityManager em;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public UpdateUserMessageProcessor()
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
    User user = ((UpdateUserLog) message.getObject()).getUser();
    String accountName = user.getLoginName();
    boolean enabled = user.getActivityStatus().equals("Active");

    CriteriaBuilder cb = this.em.getCriteriaBuilder();
    CriteriaQuery<AcquireUserInformation> criteria =
        cb.createQuery(AcquireUserInformation.class);
    Root<AcquireUserInformation> root =
        criteria.from(AcquireUserInformation.class);
    criteria.select(root);
    criteria.where(cb.equal(root.get(AcquireUserInformation_.identityId),
        accountName));

    AcquireUserInformation userInfo = null;

    try
    {
      userInfo = this.em.createQuery(criteria).getSingleResult();
    }
    catch (NoResultException e)
    {
      userInfo = new AcquireUserInformation();
      userInfo.setIdentityId(accountName);
      userInfo.setSuperAdmin(false);
      userInfo.setCaTissueUser(true);
      this.em.persist(userInfo);
    }

    userInfo.setFirstName(user.getFirstName());
    userInfo.setLastName(user.getLastName());
    userInfo.setIdentityId(accountName);
    userInfo.setEnabled(enabled);
    /*
     * This will need to be modified in the future when multiple programs are in
     * use
     */
    userInfo.setTcrb(true);
    userInfo.setInstitution(user.getInstitution().getName());
    userInfo.setAffiliation(user.getCancerResearchGroup().getName());  
    UsAddress address = new UsAddress();
    address.setStreet1(user.getAddress().getStreet());
    address.setCity(user.getAddress().getCity());
    if (user.getAddress().getState() != null)
    {
      address.setState(UsStates.getByFullName(user.getAddress().getState()));
      address.setZip(user.getAddress().getZipCode());
    }
    userInfo.setAddress(address);
    EmailAddress email = new EmailAddress();
    email.setValue(user.getEmailAddress());
    userInfo.setEmail(email);

  }

}
