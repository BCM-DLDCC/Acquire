/**
 * 
 */
package edu.bcm.dldcc.big.acquire.admin;

import java.util.List;

import javax.ejb.Local;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.events.PostAuthenticateEvent;
import org.picketlink.idm.api.query.QueryException;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.acquire.inventory.data.EntityAdaptor;
import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.security.exception.AddRelationshipException;
import edu.bcm.dldcc.big.security.exception.AddUserException;
import edu.bcm.dldcc.big.security.exception.ChangePasswordException;
import edu.bcm.dldcc.big.security.exception.UserAlreadyExistsException;
import edu.bcm.dldcc.big.security.exception.UserNotFoundException;
import edu.bcm.dldcc.big.security.model.UserInformation;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.security.exception.SMException;

/**
 * Provides for creation, editing, saving, and canceling of edits of users.
 * Referenced in EL as "userManager"
 * @author pew
 *
 */
@ConversationScoped
@Local
public interface UserManager
{
  /**
   * Initializes the system to edit an existing user
   */
  void editUser(AcquireUserInformation edit);
  
  /**
   * Saves changes made to a user record.
   * 
   * @throws IdentityException 
   * @throws SMException 
   */
  void saveUser() throws IdentityException, SMException;
  
  /**
   * Cancels changes made to a user record.
   */
  void cancelUser();
  
  /**
   * Provides a list of Collection Sites within the system.
   * 
   * @return
   */
  @Produces
  List<EntityAdaptor<Site>> getCollectionSites();
  
  /**
   * Getter for the user object
   * 
   * @return
   */
  AcquireUserInformation getUser();
  
  /**
   * Setter for the user object
   * @param user
   */
  void setUser(AcquireUserInformation user);
  
  /**
   * Get the list of all users in the application
   * 
   * @return
   */
  List<AcquireUserInformation> getUsers();
  
  /**
   * Get the list of all requested accounts that have not yet been
   * either approved or rejected.
   * 
   * @return
   */
  List<AcquireUserInformation> getPendingUsers();
  
  /**
   * Request an account for Acquire using the information in the user object.
   */
  void requestAccount();
  
  /**
   * This method will remove a pending user from the system.
   * 
   * @param user The pending user to be rejected.
   */
  void rejectAccount(AcquireUserInformation user);
  
  /**
   * Method to approve an account request and to make it active in the system.
   * Will also notify the requester that their account has been created.
   * 
   * @param user
   * @throws UserAlreadyExistsException
   * @throws AddUserException
   * @throws UserNotFoundException
   * @throws AddRelationshipException
   */
  void approveAccount(AcquireUserInformation user)
      throws UserAlreadyExistsException, AddUserException,
      UserNotFoundException, AddRelationshipException;
  
  /**
   * Setter used in changing a password
   * 
   * @param newValue
   */
  void setNewPassword(String newValue);
  
  /**
   * Getter used in changing a password
   * @return
   */
  String getNewPassword();
  
  /**
   * Setter for authenticating when trying to change a password.
   * 
   * @param oldValue
   */
  void setOldPassword(String oldValue);
  
  /**
   * Getter for authenticating when trying to change a password.
   * @return
   */
  String getOldPassword();
  
  /**
   * Method to change a user's password as an administrative function, without
   * authenticating against the user's current password.
   * 
   * @throws UserNotFoundException
   * @throws ChangePasswordException
   */
  void adminChangePassword() throws UserNotFoundException, ChangePasswordException;
  
  /**
   * Changes a user's password. Requires authenticating against the current
   * password
   * 
   * @throws IdentityException
   * @throws ChangePasswordException
   * @throws UserNotFoundException
   */
  void changePassword() throws IdentityException, ChangePasswordException,
      UserNotFoundException;
  
  /**
   * Retrieves a list of users in the system with the given role in any 
   * program or site.
   * 
   * @param roleName
   * @return
   * @throws FeatureNotSupportedException
   * @throws QueryException
   */
  List<AcquireUserInformation> getUsersWithRole(String roleName)
      throws FeatureNotSupportedException, QueryException;

  /**
   * Retrieves the AcquireUserInformation object for the given username.
   * 
   * @param username
   * @return
   * @throws UserNotFoundException
   */
  AcquireUserInformation findUserByUsername(String username)
      throws UserNotFoundException;

}
