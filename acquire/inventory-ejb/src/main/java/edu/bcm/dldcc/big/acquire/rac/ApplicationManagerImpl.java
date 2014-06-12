package edu.bcm.dldcc.big.acquire.rac;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.mail.api.MailMessage;
import org.jboss.seam.mail.templating.freemarker.FreeMarkerTemplate;
import org.picketlink.idm.api.query.QueryException;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.primefaces.event.FileUploadEvent;

import edu.bcm.dldcc.big.acquire.admin.UserManager;
import edu.bcm.dldcc.big.acquire.annotations.file.FileHandler;
import edu.bcm.dldcc.big.acquire.annotations.security.authorizer.Rac;
import edu.bcm.dldcc.big.acquire.event.MailExceptionEvent;
import edu.bcm.dldcc.big.acquire.exception.rac.DeleteCommentException;
import edu.bcm.dldcc.big.acquire.qualifiers.Annotations;
import edu.bcm.dldcc.big.acquire.qualifiers.Operations;
import edu.bcm.dldcc.big.acquire.security.Authorizations;
import edu.bcm.dldcc.big.acquire.util.Utilities;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.data.AgeRange;
import edu.bcm.dldcc.big.rac.data.ApplicationStatus;
import edu.bcm.dldcc.big.rac.data.Vote;
import edu.bcm.dldcc.big.rac.entity.AgeAtCollection;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.rac.entity.Application_;
import edu.bcm.dldcc.big.rac.entity.Comment;
import edu.bcm.dldcc.big.rac.entity.InvestigatorInfo;
import edu.bcm.dldcc.big.rac.entity.VoteRecord;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.bcm.dldcc.big.util.qualifier.Current;
import edu.bcm.dldcc.big.utility.entity.FileEntry;

/**
 * Session Bean implementation class ApplicationManagerImpl
 */
@Stateful
@ConversationScoped
@Named("applicationManager")
public class ApplicationManagerImpl implements ApplicationManager,
    SessionSynchronization
{

  private Application application;

  private AcquireUserInformation user;

  private Vote vote;

  private Long applicationId;

  private AgeRange range;

  private SearchOperator operator;

  private List<MailMessage> emails = new ArrayList<MailMessage>();

  @Inject
  @Operations
  @Annotations
  private EntityManager entityManager;

  @Inject
  @Current
  private AcquireUserInformation currentUser;

  @Inject
  private Authorizations authorizations;

  @Inject
  @Named("currentProgram")
  private String program;

  @Inject
  private FileHandler files;

  @Inject
  private Instance<MailMessage> mail;

  @Inject
  private Messages messages;

  @Inject
  private InternetAddress support;

  @Inject
  @Named("serverName")
  private String server;

  @Inject
  private UserManager userManager;

  @Inject
  private Event<MailExceptionEvent> event;

  @Inject
  @Named("currentProgram")
  private String currentProgram;

  @Inject
  private RacEmailTemplateProvider emailProvider;

  @Inject
  private Utilities util;

  @Inject
  private ExternalContext context;

  private boolean submitting = false;

  /**
   * Default constructor.
   */
  public ApplicationManagerImpl()
  {
    super();
  }

  public static Logger log = Logger.getLogger(ApplicationManagerImpl.class);

  @Override
  public void setApplication(Application current)
  {
    this.application = current;
  }

  @Override
  public Application getApplication()
  {
    return this.application;
  }

  @Override
  @Rac
  public List<Application> getInbox()
  {
    return this.getApplicationByStatus(ApplicationStatus.getInboxStatus());
  }

  @Override
  @Rac
  public List<Application> getArchive()
  {
    return this.getApplicationByStatus(ApplicationStatus.getArchivedStatus());
  }

  private List<Application> getApplicationByStatus(
      Set<ApplicationStatus> statusSet)
  {
    CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
    CriteriaQuery<Application> criteria = cb.createQuery(Application.class);
    Root<Application> root = criteria.from(Application.class);
    criteria.select(root);
    criteria.where(this.getWhereClause(root, statusSet));
    return this.entityManager.createQuery(criteria).getResultList();
  }

  private Predicate getWhereClause(Root<Application> root,
      Set<ApplicationStatus> statusSet)
  {
    Predicate predicate =
        root.get(Application_.applicationStatus).in(statusSet);
    try
    {
      if (!authorizations.isRacChair(this.program))
      {
        predicate =
            this.entityManager.getCriteriaBuilder().and(
                predicate,
                this.entityManager.getCriteriaBuilder().equal(
                    root.join(Application_.reviewers).key(), this.currentUser));
      }
    }
    catch (IdentityException e)
    {
      /*
       * If we can't check permissions, we assume the user isn't RAC Chair, so
       * we don't add to the predicate
       */

    }

    return predicate;

  }

  @Override
  @Produces
  @RequestScoped
  @Named("userApplications")
  public List<Application> getUserApplications()
  {
    List<Application> applications = new ArrayList<Application>();
    if (this.currentUser.getIdentityId() != null
        && !this.currentUser.getIdentityId().isEmpty())
    {
      CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
      CriteriaQuery<Application> criteria = cb.createQuery(Application.class);
      Root<Application> root = criteria.from(Application.class);
      criteria.select(root);
      criteria.where(cb.equal(root.get(Application_.owningUser),
          this.currentUser));
      applications = this.entityManager.createQuery(criteria).getResultList();
    }
    return applications;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#vote(edu.bcm.dldcc.big
   * .admin.entity.AcquireUserInformation, java.lang.Boolean)
   */
  @Override
  public void processVote(AcquireUserInformation user)
  {
    VoteRecord record = this.getApplication().getReviewers().get(user);
    record.setVote(this.getVote());
    record.setRecordingUser(this.currentUser);
    this.entityManager.flush();
    this.messages.info("Vote processed for " + user.getFullName());
  }
  
  @Override
  public void processVote(AcquireUserInformation user, Application application)
  {
    this.setApplication(application);
    this.processVote(user);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#setUser(edu.bcm.dldcc.
   * big.admin.entity.AcquireUserInformation)
   */
  @Override
  public void setUser(AcquireUserInformation user)
  {
    this.user = user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#getUser()
   */
  @Override
  public AcquireUserInformation getUser()
  {
    return this.user;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#setVote(java.lang.Boolean)
   */
  @Override
  public void setVote(Vote vote)
  {
    this.vote = vote;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#getVote()
   */
  @Override
  public Vote getVote()
  {
    return this.vote;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#addReviewDocument(org.
   * primefaces.event.FileUploadEvent)
   */
  @Override
  public void addReviewDocument(FileUploadEvent event)
  {
    this.getApplication().getReviewDocuments().add(files.uploadFile(event));
    this.entityManager.flush();
  }

  @Override
  public void removeReviewDocument(FileEntry entry)
  {
    this.getApplication().getReviewDocuments().remove(entry);
    this.files.removeFile(entry);
  }

  @Override
  public void removeSupportingDocument(FileEntry entry)
  {
    this.getApplication().getSupportingDocuments().remove(entry);
    this.files.removeFile(entry);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#addSupportingDocument(
   * org.primefaces.event.FileUploadEvent)
   */
  @Override
  public void addSupportingDocument(FileUploadEvent event)
  {
    this.getApplication().getSupportingDocuments().add(files.uploadFile(event));
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#saveApplication()
   */
  @Override
  public void saveApplication()
  {
    Application current = persistOrMergeApplication();
    current.removeEmptyComments();
    ApplicationStatus status = current.getApplicationStatus();
    if (status == ApplicationStatus.DRAFT)
    {
      MailMessage message = this.mail.get().from(support);

      message.to(this.currentUser.getEmail().getValue()).subject(
          "Your application to TCRB has been saved");
      message
          .bodyHtml(
              new FreeMarkerTemplate(this.emailProvider
                  .getApplicationSaveEmail()))
          .put("name", this.currentUser.getFullName())
          .put("title", current.getProject().getProjectTitle())
          .put("server", this.server + this.context.getRequestContextPath());
      message.mergeTemplates();
      this.emails.add(message);
    }
    else if (status == ApplicationStatus.SUBMITTED
        || status == ApplicationStatus.IN_REVIEW)
    {
      this.notifyRacCoordinator(Operation.SAVE);
    }

    this.messages.info("Your application has been saved.");

  }

  private void notifyRacCoordinator(Operation operation)
  {
    try
    {

      List<AcquireUserInformation> notify =
          this.userManager.getUsersWithRole("RAC Chair");
      List<String> emailAddresses = new ArrayList<String>();
      for (AcquireUserInformation current : notify)
      {
        emailAddresses.add(current.getEmail().getValue());
      }
      MailMessage message = this.mail.get();
      message.to(emailAddresses.toArray(new String[]
      {}));
      message.from(this.currentUser.getEmail().getValue());
      if (operation == Operation.SUBMIT)
      {
        message.subject("Application status changed");
        message
            .bodyHtml(
                new FreeMarkerTemplate(this.emailProvider
                    .getCoordinatorSubmitEmail()))
            .put("name", this.getApplication().getProject().getProjectTitle())
            .put("status", this.getApplication().getApplicationStatus())
            .put("server", this.server + this.context.getRequestContextPath());
      }
      else
      {
        message.subject("Submitted Application Updated");
        message.to(this.getApplication().getOwningUser().getEmail().getValue());
        message
            .bodyHtml(
                new FreeMarkerTemplate(this.emailProvider
                    .getPostSubmitSaveEmail()))
            .put("title", this.getApplication().getProject().getProjectTitle())
            .put("name", this.currentUser.getFullName())
            .put("server", this.server + this.context.getRequestContextPath());

      }

      message.mergeTemplates();
      this.emails.add(message);

    }
    catch (FeatureNotSupportedException e)
    {
      this.event.fire(new MailExceptionEvent(
          "Failed to determine RAC information on submission", e));
    }
    catch (QueryException e)
    {
      this.event.fire(new MailExceptionEvent(
          "Failed to determine RAC information on submission", e));
    }
  }

  /**
   * @return
   */
  protected Application persistOrMergeApplication()
  {
    Application current = this.getApplication();
    if (current.getId() == null)
    {
      this.entityManager.persist(current);

    }
    else if (!this.entityManager.contains(current))
    {
      this.setApplication(this.entityManager.merge(current));
    }
    return current;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#submitApplication()
   */
  @Override
  public void submitApplication()
  {
    System.out.println("Submitting");
    Application current = this.persistOrMergeApplication();

    current.setApplicationStatus(ApplicationStatus.SUBMITTED);
    current.setSubmissionDate(new Date());

    MailMessage message =
        this.mail.get().from(support)
            .to(current.getInvestigator().getEmail().getValue())
            .subject("Your application to TCRB has been submitted");
    message
        .bodyHtml(
            new FreeMarkerTemplate(this.emailProvider.getUserSubmitEmail()))
        .put("name", this.currentUser.getFullName())
        .put("title", current.getProject().getProjectTitle());
    message.mergeTemplates();
    this.emails.add(message);

    this.notifyRacCoordinator(Operation.SUBMIT);

    this.messages.info("Your application has been submitted.");

  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#newApplication()
   */
  @Override
  public void newApplication()
  {
    /*
     * Check for conversation here, to avoid timing issues.
     */
    this.util.startConversation();
    this.setApplication(new Application());

    InvestigatorInfo investigator = new InvestigatorInfo();
    investigator.setFirstName(this.currentUser.getFirstName());
    investigator.setLastName(this.currentUser.getLastName());
    investigator.setEmail(this.currentUser.getEmail());
    investigator.setAddress(this.currentUser.getAddress());
    this.getApplication().setInvestigator(investigator);
    this.getApplication().setOwningUser(this.currentUser);

    this.messages.info("You have begun a new Application.");
    log.info(" init done app=" + this.application.toString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#addComment()
   */
  @Override
  public void addComment()
  {
    this.getApplication().addComment(this.newCommentForCurrentUser());
  }

  @Override
  public void deleteComment(Comment comment) throws DeleteCommentException
  {
    this.validateDelete(comment);
    this.getApplication().getComments().remove(comment);

  }

  @Override
  public void deleteChildComment(Comment comment, Comment parent)
      throws DeleteCommentException
  {
    this.validateDelete(comment);
    parent.getChildren().remove(comment);

  }

  private void validateDelete(Comment comment) throws DeleteCommentException
  {
    if (!comment.getChildren().isEmpty())
    {
      throw new DeleteCommentException("A comment cannot be deleted if it has"
          + " children");
    }

    try
    {
      if (!comment.getUser().equals(this.currentUser)
          || !this.authorizations.isRacChair(this.currentProgram))
      {
        throw new DeleteCommentException("You are not authorized to delete"
            + " this comment");
      }
    }
    catch (IdentityException e)
    {
      /*
       * Role of the current user could not be determined, and it is not the
       * user who created the comment, so delete is not allowed.
       */
      throw new DeleteCommentException("You are not authorized to delete"
          + "this comment");
    }
  }

  /**
   * @return
   */
  private Comment newCommentForCurrentUser()
  {
    Comment comment = new Comment();
    comment.setText("");
    comment.setUser(this.currentUser);
    return comment;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#addChildComment(edu.bcm
   * .dldcc.big.rac.entity.Comment)
   */
  @Override
  public void addChildComment(Comment parent)
  {
    parent.addChild(this.newCommentForCurrentUser());
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#addAgeRange()
   */
  @Override
  public void addAgeRange()
  {
    this.getApplication().getMaterialRequests().getAgeAtCollection()
        .add(new AgeAtCollection(this.getOperator(), this.getRange()));
    this.setOperator(null);
    this.setRange(null);

  }

  @Override
  public void removeAgeRange(AgeAtCollection range)
  {
    this.getApplication().getMaterialRequests().getAgeAtCollection()
        .remove(range);
  }

  private enum Operation
  {
    SUBMIT,
    SAVE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#saveComments()
   */
  @Override
  public void processComment(Comment comment)
  {
    comment.setTimestamp(new Date());
    comment.removeEmptyComments();
    this.entityManager.flush();
    this.messages.info("Your comment has been saved");

    List<String> emailAddresses = new ArrayList<String>();
    for (AcquireUserInformation current : this.getApplication().getReviewers()
        .keySet())
    {
      emailAddresses.add(current.getEmail().getValue());
    }
    MailMessage message = this.mail.get();
    message.to(emailAddresses.toArray(new String[]
    {}));
    message.from(this.currentUser.getEmail().getValue());

    message.subject("New Comment on "
        + this.getApplication().getProject().getProjectTitle());
    message
        .bodyHtml(
            new FreeMarkerTemplate(this.emailProvider.getCommentNotifyEmail()))
        .put("comment", comment.getText())
        .put(
            "link",
            this.server + this.context.getRequestContextPath()
                + "/apply.jsf?id=" + this.getApplication().getId());
    message.mergeTemplates();
    this.emails.add(message);

    // TODO 2 delete or pew to merge accorindly
    if (!this.hasNewComment())
    {
      this.addComment();

    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * edu.bcm.dldcc.big.acquire.rac.ApplicationManager#setApplicationId(java.
   * lang.Long)
   */
  @Override
  public void setApplicationId(Long id)
  {
    this.applicationId = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#getApplicationId()
   */
  @Override
  public Long getApplicationId()
  {
    return this.applicationId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see edu.bcm.dldcc.big.acquire.rac.ApplicationManager#initApplication()
   */
  @Override
  public void initApplication()
  {
    this.util.startConversation();
    if (this.getApplicationId() != null && this.getApplicationId() != 0)
    {
      this.setApplication(this.entityManager.find(Application.class,
          this.getApplicationId()));
    }
    else
    {
      this.newApplication();
    }
    try
    {
      if (this.authorizations.isRac(this.currentProgram)
          && this.getApplication().getApplicationStatus() 
          != ApplicationStatus.DRAFT)
      {

        if (!hasNewComment())
        {
          this.addComment();

        }
      }
    }
    catch (IdentityException e)
    {
      /*
       * If we can't determine if the user is a RAC user, don't add the comment.
       * 
       * @TODO Consider logging this error.
       */
    }
  }

  /**
   * Becos of the way comments are added, need way to determine if there is a
   * new comment in the list
   */
  private boolean hasNewComment()
  {

    if (this.getApplication().getComments().size() == 0)
      return false;

    for (Comment comment : this.getApplication().getComments())
      if (comment.getTimestamp() == null)
        return true;

    return false;
  }

  @Override
  public boolean isApplicationEditable()
  {
    boolean editable = false;
    switch (this.getApplication().getApplicationStatus())
    {
    case DRAFT:
    {
      editable = this.getApplication().getOwningUser().equals(this.currentUser);
      break;
    }
    case SUBMITTED:
    case IN_REVIEW:
    {
      try
      {
        editable =
            this.getApplication().getOwningUser().equals(this.currentUser)
                || this.authorizations.isRacChair(this.currentProgram);
      }
      catch (IdentityException e)
      {
        /*
         * If we can't check the role of the user, assume they can't edit
         */
        editable = false;
      }
      break;
    }
    default:
    {
      editable = false;
    }
    }
    return editable;
  }

  @Override
  public void signApplication()
  {
    this.getApplication().getCertification()
        .signCertification(this.currentUser);
    this.messages.info("Application signed as "
        + this.currentUser.getFullName());
  }

  @Override
  public void validateApplicationStatus(FacesContext context,
      UIComponent toValidate, Object value)
  {
    boolean valid = true;
    ApplicationStatus status = (ApplicationStatus) value;
    switch (status)
    {
    case DRAFT:
    case SUBMITTED:
    {
      valid = false;
      break;
    }
    case IN_REVIEW:
    {
      if (EnumSet.of(ApplicationStatus.DRAFT, ApplicationStatus.SUBMITTED)
          .contains(this.getApplication().getApplicationStatus()))
      {
        valid = false;
      }
      break;
    }
    default:
    {
      valid = true;
    }
    }

    if (!valid)
    {
      ((UIInput) toValidate).setValid(false);
      FacesMessage message =
          new FacesMessage("The selected status cannot"
              + " be set manually at this time.");
      message.setSeverity(FacesMessage.SEVERITY_ERROR);
      context.addMessage(toValidate.getClientId(), message);
      // throw new ValidatorException(message);
    }

  }

  /**
   * @return the submitting
   */
  @Override
  public boolean isSubmitting()
  {
    return this.submitting;
  }

  /**
   * @param submitting
   *          the submitting to set
   */
  @Override
  public void setSubmitting(boolean submitting)
  {
    System.out.println("Setting submit status: " + submitting);
    this.submitting = submitting;
  }

  @Override
  public void validateRequiredOnSubmit(FacesContext context,
      UIComponent toValidate, Object value)
  {
    System.out.println("Validating on submit");
    if (!this.isSubmitting())
    {
      return;
    }

    if (UIInput.isEmpty(value) || value.toString().matches("\\s+"))
    {
      FacesMessage msg =
          new FacesMessage(((UIInput) toValidate).getRequiredMessage());
      msg.setSeverity(FacesMessage.SEVERITY_ERROR);
      context.addMessage(toValidate.getClientId(), msg);
      ((UIInput) toValidate).setValid(false);

    }
  }

  @Override
  public void validateCertification(FacesContext context,
      UIComponent toValidate, Object value)
  {
    if (this.isSubmitting()
        && (UIInput.isEmpty(value) || !Boolean.parseBoolean(value.toString())))
    {
      FacesMessage message =
          new FacesMessage("You must agree to the policies and procedures "
              + "before submitting your application");
      message.setSeverity(FacesMessage.SEVERITY_ERROR);
      context.addMessage(toValidate.getClientId(), message);
      ((UIInput) toValidate).setValid(false);

    }
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

  /**
   * @return the range
   */
  @Override
  public AgeRange getRange()
  {
    return this.range;
  }

  /**
   * @param range
   *          the range to set
   */
  @Override
  public void setRange(AgeRange range)
  {
    this.range = range;
  }

  /**
   * @return the operator
   */
  @Override
  public SearchOperator getOperator()
  {
    return this.operator;
  }

  /**
   * @param operator
   *          the operator to set
   */
  @Override
  public void setOperator(SearchOperator operator)
  {
    this.operator = operator;
  }

}
