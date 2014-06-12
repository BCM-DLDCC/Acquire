/**
 * 
 */
package edu.bcm.dldcc.big.acquire.admin.session;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.mail.templating.freemarker.FreeMarkerTemplate;
import org.jboss.seam.security.Identity;
import org.jboss.solder.resourceLoader.Resource;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.query.QueryException;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.admin.UserManager;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.SuperAdmin;
import edu.bcm.dldcc.big.acquire.inventory.EntityResolver;
import edu.bcm.dldcc.big.acquire.inventory.data.EntityAdaptor;
import edu.bcm.dldcc.big.acquire.util.Resources;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation_;
import edu.bcm.dldcc.big.security.controller.AdminUtility;
import edu.bcm.dldcc.big.security.exception.AddRelationshipException;
import edu.bcm.dldcc.big.security.exception.AddUserException;
import edu.bcm.dldcc.big.security.exception.ChangePasswordException;
import edu.bcm.dldcc.big.security.exception.UserAlreadyExistsException;
import edu.bcm.dldcc.big.security.exception.UserNotFoundException;
import edu.bcm.dldcc.big.util.qualifier.Current;
import edu.bcm.dldcc.big.util.qualifier.UserDatabase;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.security.exception.SMException;

/**
 * @author pew
 * 
 */
@Stateful
@Named("userManager")
@ConversationScoped
public class UserManagerImpl implements UserManager, SessionSynchronization
{
  @Inject
  @UserDatabase
  private EntityManager entityManager;

  private AcquireUserInformation user = new AcquireUserInformation();

  private String newPassword = "";

  private String oldPassword = "";
  
  private List<MailMessage> emails = new ArrayList<MailMessage>();

  @Inject
  private Utilities utilities;

  @Inject
  private EntityResolver resolver;

  @Inject
  private Instance<MailMessage> mail;

  @Inject
  private InternetAddress support;

  @Inject
  @Named("serverName")
  private String server;

  @Inject
  private ExternalContext context;

  @Inject
  private Messages messages;

  @Inject
  private AdminUtility admin;

  @Inject
  private Identity identity;

  @Inject
  private IdentitySession idSession;

  @Inject
  @Current
  private AcquireUserInformation currentUser;

  /**
   * 
   */
  public UserManagerImpl()
  {
    super();
  }

  @Override
  public List<EntityAdaptor<Site>> getCollectionSites()
  {
    return resolver.getCaTissueEntityList(Site.class, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.UserManager#editUser()
   */
  @Override
  public void editUser(AcquireUserInformation edit)
  {
    this.utilities.startConversation();
    this.setUser(edit);
  }

  @Override
  public void requestAccount()
  {
    this.setUser(new AcquireUserInformation());
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.UserManager#saveUser()
   */
  @Override
  public void saveUser() throws IdentityException, SMException
  {

    if (!this.getUser().getCaTissueUser()
        && !this.entityManager.contains(this.getUser()))
    {
      AcquireUserInformation requestor = this.getUser();
      this.entityManager.persist(requestor);
      this.messages.info("An account has been requested for you. You will "
          + "be notified when the account has been created.");
      StringBuilder sb = new StringBuilder();
      sb.append("Requestor: ");
      sb.append(requestor.getFirstName() + " " + requestor.getLastName());
      sb.append("\n");
      sb.append("From " + requestor.getInstitution() + "\n");
      sb.append("Requestor is affiliated with TCRB: " + requestor.getTcrb());
      if (requestor.getTcrb())
      {
        sb.append(" at " + requestor.getAffiliation());
      }

      mail.get()
          .from(this.getUser().getEmail().getValue())
          .to(support)
          .subject(
              "An account has been requested for Acquire on: " + this.server)
          .bodyText(sb.toString()).send();

    }
    else
    {
      this.entityManager.flush();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.UserManager#cancelUser()
   */
  @Override
  public void cancelUser()
  {
    this.utilities.endConversation();
  }

  /**
   * @return the user
   */
  public AcquireUserInformation getUser()
  {
    return this.user;
  }

  /**
   * @param user
   *          the user to set
   */
  public void setUser(AcquireUserInformation user)
  {
    this.user = user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.admin.UserManager#getUsers()
   */
  @Override
  @Produces
  @RequestScoped
  @Named("allUsers")
  public List<AcquireUserInformation> getUsers()
  {
    return this.fetchUsers(false);
  }

  @Override
  @Produces
  @Named("pendingUsers")
  public List<AcquireUserInformation> getPendingUsers()
  {
    return this.fetchUsers(true);
  }

  private List<AcquireUserInformation> fetchUsers(boolean pending)
  {
    CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
    CriteriaQuery<AcquireUserInformation> criteria =
        cb.createQuery(AcquireUserInformation.class);
    Root<AcquireUserInformation> root =
        criteria.from(AcquireUserInformation.class);
    if (pending)
    {
      criteria.where(cb.isNull(root.get(AcquireUserInformation_.identityId)));
    }
    else
    {
      criteria
          .where(cb.isNotNull(root.get(AcquireUserInformation_.identityId)));
    }
    criteria.orderBy(cb.asc(root.get(AcquireUserInformation_.lastName)));

    return this.entityManager.createQuery(criteria).getResultList();
  }

  @Override
  public void rejectAccount(AcquireUserInformation user)
  {
    if (!user.getCaTissueUser()
        && (user.getIdentityId() == null || user.getIdentityId().equals("")))
    {
      this.entityManager.remove(user);
      this.entityManager.flush();
      this.messages.info("User request rejected. Please contact requester");
    }
  }

  @Override
  public void approveAccount(AcquireUserInformation user)
      throws UserAlreadyExistsException, AddUserException,
      UserNotFoundException, AddRelationshipException
  {
    String password = this.admin.addUser(user.getEmail().getValue());
    user.setIdentityId(user.getEmail().getValue());
    user.setEnabled(true);
    this.entityManager.flush();
    this.admin.addRelationship(user.getEmail().getValue(), "Public Data",
        "TCRB", Resources.TYPE_PROGRAM);
    this.messages
        .info("User approved and account created with Public Data role");
    InputStream template = this.getClass().getResourceAsStream(
        "/templates/accountRequestEmailTemplate");

    MailMessage message =
        mail.get()
            .from(this.support)
            .to(user.getEmail().getValue())
            .subject(
                "[secure] Your request for an account on Acquire has been approved")
            .bodyHtml(
                new FreeMarkerTemplate(template))
            .put("name", user.getFullName())
            .put("username", user.getIdentityId()).put("password", password)
            .put("server", this.server + this.context.getRequestContextPath())
            .put("bcm", user.getBcmUser());
    message.mergeTemplates();
    this.emails.add(message);
    try
    {
      template.close();
    }
    catch (IOException e)
    {
      // Can't close the stream. Do nothing.
    }

  }

  /**
   * @return the newPassword
   */
  @Override
  public String getNewPassword()
  {
    return this.newPassword;
  }

  /**
   * @param newPassword
   *          the newPassword to set
   */
  @Override
  public void setNewPassword(String newPassword)
  {
    this.newPassword = newPassword;
  }

  /**
   * @return the oldPassword
   */
  @Override
  public String getOldPassword()
  {
    return this.oldPassword;
  }

  /**
   * @param oldPassword
   *          the oldPassword to set
   */
  @Override
  public void setOldPassword(String oldPassword)
  {
    this.oldPassword = oldPassword;
  }

  @Override
  @SuperAdmin
  public void adminChangePassword() throws UserNotFoundException,
      ChangePasswordException
  {
    this.admin.changePassword(this.getUser().getIdentityId(),
        this.getNewPassword());
    this.messages.info("Password successfully changed");

  }

  @Override
  public void changePassword() throws IdentityException, UserNotFoundException
  {
    try
    {
      if (!this.idSession.getAttributesManager().validatePassword(
          this.identity.getUser(), this.getOldPassword()))
      {
        this.messages.error("Unable to confirm your identity.");
      }

      this.admin.changePassword(this.currentUser.getIdentityId(),
          this.getNewPassword());
      this.messages.info("Password successfully changed");
    }
    catch (ChangePasswordException e)
    {
      this.messages.error(e.getMessage());
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.admin.UserManager#getUsersWithRole(java.lang.
   * String)
   */
  @Override
  public List<AcquireUserInformation> getUsersWithRole(String roleName)
      throws FeatureNotSupportedException, QueryException
  {
    return this.admin.getUsersForRole(roleName, AcquireUserInformation.class);
  }

  @Override
  public AcquireUserInformation findUserByUsername(String username)
      throws UserNotFoundException
  {
    return this.admin.findUserByIdentityId(username,
        AcquireUserInformation.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see javax.ejb.SessionSynchronization#afterBegin()
   */
  @Override
  public void afterBegin() throws EJBException, RemoteException
  {
    // Empty method, nothing really to do here

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.ejb.SessionSynchronization#afterCompletion(boolean)
   */
  @Override
  public void afterCompletion(boolean committed) throws EJBException,
      RemoteException
  {
    if (committed)
    {
      for (MailMessage message : this.emails)
      {
        message.send();
      }
      this.emails.clear();
    }
    else
    {
      this.emails.clear();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.ejb.SessionSynchronization#beforeCompletion()
   */
  @Override
  public void beforeCompletion() throws EJBException, RemoteException
  {
    // Empty method, nothing really to do here

  }

}
