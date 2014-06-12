/**
 * 
 */
package edu.bcm.dldcc.big.acquire.rac;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.international.status.Messages;

import edu.bcm.dldcc.big.acquire.admin.UserManager;
import edu.bcm.dldcc.big.acquire.exception.rac.ReviewerNotAssignedToApplicationException;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.data.Vote;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.rac.entity.VoteRecord;
import edu.bcm.dldcc.big.security.exception.UserNotFoundException;

/**
 * @author pew
 * 
 */
@Stateful
@ViewScoped
@Named("voteManager")
public class VoteManagerImpl implements VoteManager, Serializable
{

  private String reviewer;
  private Long applicationId;
  private Date expiration;
  private boolean vote;
  private Application application;
  private AcquireUserInformation reviewerInfo;

  @Inject
  @Annotations
  @Operations
  private EntityManager em;

  @Inject
  private Messages messages;

  @Inject
  private UserManager userManager;
  
  @Inject
  private Event<AcquireUserInformation> userEvent;

  /**
   * 
   */
  public VoteManagerImpl()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.VoteManager#setReviewer(java.lang.String)
   */
  @Override
  public void setReviewer(String username) throws UserNotFoundException
  {
    this.reviewer = username;
    this.setReviewerInfo();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#getReviewer()
   */
  @Override
  public String getReviewer()
  {
    return this.reviewer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.VoteManager#setApplicationId(java.lang.Long)
   */
  @Override
  public void setApplicationId(Long id)
  {
    this.applicationId = id;
    this.setApplication();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#getApplicationId()
   */
  @Override
  public Long getApplicationId()
  {
    return this.applicationId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.VoteManager#setExpiration(java.util.Date)
   */
  @Override
  public void setExpiration(Date expire)
  {
    this.expiration = expire;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#getExpiration()
   */
  @Override
  public Date getExpiration()
  {
    return this.expiration;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#setVote(boolean)
   */
  @Override
  public void setVote(boolean vote)
  {
    this.vote = vote;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#getVote()
   */
  @Override
  public boolean getVote()
  {
    return this.vote;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#processVote()
   */
  @Override
  public void processVote() throws ReviewerNotAssignedToApplicationException
  {
    this.validateReviewerAssignedToApplication();
    VoteRecord record = this.getApplication().getReviewers()
        .get(this.getReviewerInfo());
    record.setVote(Vote.valueForBoolean(this.getVote()));
    record.setRecordingUser(this.getReviewerInfo());
    this.userEvent.fire(this.getReviewerInfo());
    this.em.flush();
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.VoteManager#cancelVote()
   */
  @Override
  public void cancelVote() throws ReviewerNotAssignedToApplicationException
  {
    this.validateReviewerAssignedToApplication();
    VoteRecord record = this.getApplication().getReviewers()
        .get(this.getReviewerInfo());
    record.setVote(Vote.NOT_VOTED);
    record.setRecordingUser(null);
    this.userEvent.fire(this.getReviewerInfo());
    this.em.flush();
    this.messages.info("Your vote has been cancelled.");

  }

  @Override
  public Application getApplication()
  {
    return this.application;
  }

  private void setApplication()
  {
    this.application = this.em.find(Application.class, this.getApplicationId());
  }

  @Override
  public AcquireUserInformation getReviewerInfo()
  {
    return this.reviewerInfo;
  }

  private void setReviewerInfo() throws UserNotFoundException
  {
    this.reviewerInfo = this.userManager.findUserByUsername(this.getReviewer());
  }

  private void validateReviewerAssignedToApplication()
      throws ReviewerNotAssignedToApplicationException
  {
    if (!this.getApplication().getReviewers()
        .containsKey(this.getReviewerInfo()))
    {
      throw new ReviewerNotAssignedToApplicationException(
          "You have not been assigned as a reviewer for this application.");
    }
  }
}
