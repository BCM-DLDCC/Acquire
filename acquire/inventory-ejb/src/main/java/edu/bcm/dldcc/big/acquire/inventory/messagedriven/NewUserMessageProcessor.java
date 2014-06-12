package edu.bcm.dldcc.big.acquire.inventory.messagedriven;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.qualifiers.CaTissueInstance;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation_;
import edu.bcm.dldcc.big.security.exception.UserAlreadyExistsException;
import edu.bcm.dldcc.big.util.iso21090.model.EmailAddress;
import edu.bcm.dldcc.big.util.iso21090.model.EmailAddress_;
import edu.bcm.dldcc.big.util.iso21090.model.UsAddress;
import edu.bcm.dldcc.big.util.values.UsStates;
import edu.bcm.dldcc.big.utility.entity.NewUserLog;
import edu.wustl.catissuecore.domain.User;

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
        propertyValue = "New User"),
    @ActivationConfigProperty(propertyName = "clientId",
        propertyValue = "acquire"),
    @ActivationConfigProperty(propertyName = "destination",
        propertyValue = "topic/newUser") }, mappedName = "newUserTopic")
public class NewUserMessageProcessor extends AbstractCaTissueEntityProcessor
    implements MessageListener
{

  @Inject
  private IdentitySession identity;

  @Inject
  private EntityManager em;

  /**
   * @see AbstractCaTissueEntityProcessor#AbstractCaTissueEntityProcessor()
   */
  public NewUserMessageProcessor()
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
    try
    {
      /*
       * Create a new account in Acquire
       */
      User newUser = ((NewUserLog) message.getObject()).getUser();
      String accountName = newUser.getLoginName();
      
      /*
       * Store the activity status of the new user
       */
      boolean enabled = newUser.getActivityStatus().equals("Active");
      /*
       * Check for existing AcquireUserInformation from an account request
       */
      AcquireUserInformation userInfo = null;
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<AcquireUserInformation> criteria =
          cb.createQuery(AcquireUserInformation.class);
      Root<AcquireUserInformation> root =
          criteria.from(AcquireUserInformation.class);
      criteria.select(root);
      criteria.where(cb.equal(
          root.get(AcquireUserInformation_.email).get(EmailAddress_.value),
          accountName));
      /*
       * There will be either 1 or 0. Get the result list as we expect there to
       * often not be a result, and want to avoid the exception. Then use the
       * first (and only) element in the list, if there is one.
       */
      List<AcquireUserInformation> users =
          em.createQuery(criteria).getResultList();
      if (users.size() > 0)
      {
        userInfo = users.get(0);
        if (userInfo.getIdentityId() != null
            && !userInfo.getIdentityId().equals(""))
        {
          JMSException e =  new JMSException("A user was created in caTissue" +
              "duplicating an existing Acquire user");
          e.setLinkedException(new UserAlreadyExistsException("A user was " +
          		"created in caTissue" +
          		"duplicating an existing Acquire user"));
        }
      }
      else
      {
        /*
         * No account request, so create a new entity and populate it with
         * information from caTissue
         */
        userInfo = new AcquireUserInformation();
        userInfo.setFirstName(newUser.getFirstName());
        userInfo.setLastName(newUser.getLastName());
        userInfo.setInstitution(newUser.getInstitution().getName());
        userInfo.setAffiliation(newUser.getCancerResearchGroup().getName());
        userInfo.setBcmUser(accountName.contains("@bcm.edu"));
        UsAddress address = new UsAddress();
        address.setStreet1(newUser.getAddress().getStreet());
        address.setCity(newUser.getAddress().getCity());
        address.setState(UsStates
            .getByFullName(newUser.getAddress().getState()));
        address.setZip(newUser.getAddress().getZipCode());
        userInfo.setAddress(address);
        userInfo.setSuperAdmin(false);
        EmailAddress email = new EmailAddress();
        email.setValue(newUser.getEmailAddress());
        userInfo.setEmail(email);
        this.em.persist(userInfo);
      }

      /*
       * Finish setup of AcquireUserInformation, to tie it to the IdentityObject
       * and provide additional information.
       */
      userInfo.setIdentityId(accountName);
      userInfo.setCaTissueUser(true);
      userInfo.setEnabled(enabled);
      /*
       * This will need to be modified in the future when multiple programs are
       * in use
       */
      userInfo.setTcrb(true);
      
      identity.getPersistenceManager().createUser(accountName);

    }
    catch (IdentityException e)
    {
      JMSException je = new JMSException(e.getMessage());
      je.setLinkedException(e);
      throw je;
    }

  }

}
