/**
 * 
 */
package edu.bcm.dldcc.big.acquire.listener;

import java.io.Serializable;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.PostAuthenticateEvent;

import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation_;
import edu.bcm.dldcc.big.util.qualifier.Current;
import edu.bcm.dldcc.big.util.qualifier.UserDatabase;

/**
 * Utility class to ensure necessary information is present for admin purposes.
 * 
 * Provides an AcquireUserInformation object as "currentUser" in EL, or
 * annotated @Current
 * 
 * @author pew
 * 
 */
@SessionScoped
@TransactionAttribute
public class AdminResources implements Serializable
{

  private AcquireUserInformation currentUser;

  /**
   * Default constructor
   */
  public AdminResources()
  {
    super();
  }

  @Produces
  @Current
  @Named("currentUser")
  public AcquireUserInformation getCurrentUser(
      @Operations @Annotations EntityManager em)
  {
    AcquireUserInformation value = null;
    if(this.currentUser != null)
    {
      value = em.merge(this.currentUser);
    }
    return value;
  }

  public AcquireUserInformation getCurrentUser()
  {
    return this.currentUser;
  }
  
  /**
   * Listener that sets up the current user information for use throughout the
   * application.
   * 
   * @param event
   *          PostAuthenticateEvent
   * @param identity
   * @param entityManager
   */
  public void setupCurrentUser(@Observes PostAuthenticateEvent event,
      Identity identity, @UserDatabase EntityManager entityManager)
  {
    // Get my user information
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<AcquireUserInformation> cq =
        cb.createQuery(AcquireUserInformation.class);
    Root<AcquireUserInformation> userInfo =
        cq.from(AcquireUserInformation.class);

    cq.where(cb.equal(userInfo.get(AcquireUserInformation_.identityId),
        identity.getUser().getId()));

    cq.select(userInfo);

    try
    {
      this.currentUser = entityManager.createQuery(cq).getSingleResult();
    }
    catch (NoResultException exception)
    {
      throw new IllegalArgumentException(exception.getMessage(), exception);
    }

  }

  public void setupNonLoggedInUser(@Observes AcquireUserInformation info)
  {
    this.currentUser = info;
  }

}
