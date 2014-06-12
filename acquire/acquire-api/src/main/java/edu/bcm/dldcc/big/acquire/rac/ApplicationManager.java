/**
 * 
 */
package edu.bcm.dldcc.big.acquire.rac;

import java.util.List;

import javax.ejb.Local;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

import edu.bcm.dldcc.big.acquire.exception.rac.DeleteCommentException;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.rac.data.AgeRange;
import edu.bcm.dldcc.big.rac.data.Vote;
import edu.bcm.dldcc.big.rac.entity.AgeAtCollection;
import edu.bcm.dldcc.big.rac.entity.Application;
import edu.bcm.dldcc.big.rac.entity.Comment;
import edu.bcm.dldcc.big.search.SearchOperator;
import edu.bcm.dldcc.big.utility.entity.FileEntry;

/**
 * @author pew
 *
 */
@Local
public interface ApplicationManager
{
  /**
   * Set the current Application.
   * @param current
   */
  void setApplication(Application current);
  
  /**
   * Get the current Application
   * @return
   */
  Application getApplication();
  
  /**
   * Returns a List of all non-archived Applications that are visible to the
   * user.
   * @return
   */
  List<Application> getInbox();
  
  /**
   * Returns a List of all Applications in the archive that are visible to the
   * user.
   * @return
   */
  List<Application> getArchive();
  
  /**
   * Provides a List of the current user's Applications that have been
   * saved or submitted.
   * @return
   */
  List<Application> getUserApplications();
  
  /**
   * Set the current user's information
   * @param user
   */
  void setUser(AcquireUserInformation user);
  
  /**
   * Get the information for the current user.
   * @return
   */
  AcquireUserInformation getUser();
  
  /**
   * Set the current vote object
   * @param vote
   */
  void setVote(Vote vote);
  
  /**
   * Get the current Vote object.
   * @return
   */
  Vote getVote();
  
  /**
   * Attach a file to the current application to provide additional information
   * for the reviewers.
   * @param event A FileUploadEvent containing information on the file to 
   * be uploaded.
   */
  void addReviewDocument(FileUploadEvent event);
  
  /**
   * Attach a file to the current application for use in supporting the request.
   * @param event A FileUploadEvent containing information on the file to 
   * be uploaded.
   */
  void addSupportingDocument(FileUploadEvent event);
  
  /**
   * Saves edits to the current application, without changing its status,
   * and sends notification
   */
  void saveApplication();
  
  /**
   * Submits an application to the RAC, beginning the review process.
   */
  void submitApplication();
  
  /**
   * This method will create and initialize a new application and set it
   * as the current application.
   */
  void newApplication();
  
  /**
   * This method will add a top-level comment to the current Application,
   * pre-set with the current user as the user making the comment
   */
  void addComment();
  
  /**
   * This method will add a comment as a child to the given comment,
   * pre-set with the current user as the user making the comment
   * 
   * @param parent The comment that a new response will be made to
   */
  void addChildComment(Comment parent);
  
  /**
   * Adds a new AgeAtCollection object to the current application.
   */
  void addAgeRange();
 
  /**
   * Handles all work necessary when a comment is saved.
   * @param comment The comment to process
   */
  void processComment(Comment comment);
  
  /**
   * Set the id of the current application
   * @param id
   */
  void setApplicationId(Long id);
  
  /**
   * Get the id of the current application
   * @return
   */
  Long getApplicationId();
  
  /**
   * Sets the current application based on the application id, and prepares it.
   */
  void initApplication();

  /**
   * Checks whether the current user is able to edit the application, given its
   * current status. 
   * @return
   */
  boolean isApplicationEditable();

  /**
   * Remove an AgeAtCollection selection from the current application
   * @param range
   */
  void removeAgeRange(AgeAtCollection range);

  /**
   * Remove a comment from the current application.
   * @param comment
   * @throws DeleteCommentException 
   */
  void deleteComment(Comment comment) throws DeleteCommentException;

  /**
   * Remove a comment from a parent comment.
   * @param comment
   * @param parent
   * @throws DeleteCommentException 
   */
  void deleteChildComment(Comment comment, Comment parent) throws DeleteCommentException;

  /**
   * Signs the certification of the application as the current user.
   */
  void signApplication();
  
  /**
   * Record a vote for the current user.
   */
  void processVote(AcquireUserInformation user);

  /**
   * Removes a review document from the current application.
   * @param entry The information about the file to be removed.
   */
  void removeReviewDocument(FileEntry entry);

  /**
   * Removes a supporting document from the current application.
   * @param entry THe information about the file to be removed.
   */
  void removeSupportingDocument(FileEntry entry);

  /**
   * Validation method to determine if the ApplicationStatus can be set 
   * to a specified value, based on the current status of the application.
   * 
   * @param context
   * @param toValidate
   * @param value
   */
  void validateApplicationStatus(FacesContext context, UIComponent toValidate,
      Object value);

  /**
   * Property used in validating required fields before submitting an 
   * application
   * @return
   */
  boolean isSubmitting();

  /**
   * Property used in validating required fields before submitting an 
   * application
   * @param submitting
   */
  void setSubmitting(boolean submitting);

  void validateRequiredOnSubmit(FacesContext context, UIComponent toValidate,
      Object value);

  void validateCertification(FacesContext context, UIComponent toValidate,
      Object value);

  AgeRange getRange();

  void setRange(AgeRange range);

  SearchOperator getOperator();

  void setOperator(SearchOperator operator);

  void processVote(AcquireUserInformation user, Application application);
  
  
}
