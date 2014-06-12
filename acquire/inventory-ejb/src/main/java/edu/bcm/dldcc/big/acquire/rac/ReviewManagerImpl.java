package edu.bcm.dldcc.big.acquire.rac;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.mail.templating.freemarker.FreeMarkerTemplate;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.picketlink.idm.api.query.QueryException;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.primefaces.model.DualListModel;

import edu.bcm.dldcc.big.acquire.admin.UserManager;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.data.ApplicationStatus;
import edu.bcm.dldcc.big.rac.data.Vote;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.rac.entity.VoteRecord;
import edu.bcm.dldcc.big.util.qualifier.Current;

/**
 * Session Bean implementation class ReviewManagerImpl
 */
@Stateful
@ConversationScoped
@Named("reviewerManager")
public class ReviewManagerImpl implements ReviewManager, SessionSynchronization
{

  private DualListModel<AcquireUserInformation> reviwerModel;

  private Application application;

  @Inject
  private UserManager userManager;

  @Inject
  @Current
  private AcquireUserInformation currentUser;

  @Inject
  @Named("serverName")
  private String server;

  @Inject
  private RacEmailTemplateProvider emailProvider;

  @Inject
  private ExternalContext context;

  @Inject
  private Instance<MailMessage> mail;

  @Inject
  private Messages messages;

  private List<MailMessage> emails = new ArrayList<MailMessage>();

  private List<AcquireUserInformation> racMembers;

  /**
   * Default constructor.
   */
  public ReviewManagerImpl()
  {
    super();
  }

  @PostConstruct
  public void setup()
  {
    this.reviwerModel = new DualListModel<AcquireUserInformation>();
    try
    {
      this.racMembers = this.userManager.getUsersWithRole("RAC Committee");
      this.racMembers.addAll(this.userManager.getUsersWithRole("RAC Chair"));
      this.getReviewerModel().setSource(racMembers);
    }
    catch (FeatureNotSupportedException e)
    {
      /*
       * If unable to get lists, then rethrow exception
       */
      throw new IllegalStateException("Unable to build list of reviewers", e);
    }
    catch (QueryException e)
    {
      /*
       * If unable to get lists, then rethrow exception
       */
      throw new IllegalStateException("Unable to build list of reviewers", e);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ReviewManager#setModel(org.primefaces.model
   * .DualListModel)
   */
  @Override
  public void setReviewerModel(DualListModel<AcquireUserInformation> model)
  {
    this.reviwerModel = model;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ReviewManager#getModel()
   */
  @Override
  public DualListModel<AcquireUserInformation> getReviewerModel()
  {
    return this.reviwerModel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ReviewManager#initList()
   */
  @Override
  public void initList(Application application)
  {
    List<AcquireUserInformation> source =
        new ArrayList<AcquireUserInformation>(this.racMembers);
    Set<AcquireUserInformation> currentReviewers =
        application.getReviewers().keySet();
    this.getReviewerModel().setTarget(
        new ArrayList<AcquireUserInformation>(currentReviewers));
    source.removeAll(currentReviewers);
    this.getReviewerModel().setSource(source);
    this.setApplication(application);
  }

  @Override
  public void assignReviewers()
  {
    List<AcquireUserInformation> assigned = this.getReviewerModel().getTarget();
    Map<AcquireUserInformation, VoteRecord> reviews =
        this.getApplication().getReviewers();
    Set<AcquireUserInformation> original =
        new HashSet<AcquireUserInformation>(reviews.keySet());
    for (AcquireUserInformation current : original)
    {
      if (!assigned.contains(current))
      {
        if (reviews.get(current).getVote() == Vote.NOT_VOTED)
        {
          reviews.remove(current);
        }
        else
        {
          messages.error(current.getFullName() + " cannot be removed from "
              + "this application as a reviewer because a vote already has "
              + "been recorded.");
        }

      }

    }
    this.getApplication().setApplicationStatus(ApplicationStatus.IN_REVIEW);
    assigned.removeAll(original);
    List<String> notifyEmails = new ArrayList<String>();
    for (AcquireUserInformation newReviewer : assigned)
    {
      reviews.put(newReviewer, new VoteRecord(Vote.NOT_VOTED));
      notifyEmails.add(newReviewer.getEmail().getValue());
    }

    for (String email : notifyEmails)
    {
      MailMessage message = this.mail.get();
      message.to(email);
      message.from(this.currentUser.getEmail().getValue());
      message
          .subject("[secure] You have been assigned an application to review");
      message
          .bodyHtml(
              new FreeMarkerTemplate(this.emailProvider
                  .getReviewerNotifyEmail()))
          .put("application", this.getApplication())
          .put(
              "link",
              this.server + this.context.getRequestContextPath()
                  + "/apply.jsf?id=" + this.getApplication().getId())
          .put("voteYesLink", this.buildVotingLink(true, email))
          .put("voteNoLink", this.buildVotingLink(false, email))
          .put("server", this.server + this.context.getRequestContextPath());
      message.mergeTemplates();
      this.emails.add(message);
    }

  }

  /**
   * @return the application
   */
  protected Application getApplication()
  {
    return this.application;
  }

  /**
   * @param application
   *          the application to set
   */
  protected void setApplication(Application application)
  {
    this.application = application;
  }

  private String buildVotingLink(boolean vote, String reviewer)
  {
    Instant expire = new Instant(new Date()).plus(Duration.standardDays(30));
    StringBuilder link =
        new StringBuilder(this.server + this.context.getRequestContextPath());
    link.append("/public/vote.jsf");
    link.append("?username=");
    link.append(reviewer);
    link.append("&applicationId=");
    link.append(this.getApplication().getId());
    link.append("&vote=");
    link.append(vote);
    link.append("&expire=");
    link.append(expire.toDate().getTime());
    return link.toString();
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
