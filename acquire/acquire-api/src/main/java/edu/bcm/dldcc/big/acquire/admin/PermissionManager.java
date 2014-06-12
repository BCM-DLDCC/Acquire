/**
 * 
 */
package edu.bcm.dldcc.big.acquire.admin;

import java.util.List;

import javax.ejb.Local;
import javax.enterprise.context.SessionScoped;

import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

import edu.bcm.dldcc.big.admin.entity.AcquireUserInformation;
import edu.bcm.dldcc.big.security.exception.AddRelationshipException;
import edu.bcm.dldcc.big.security.exception.UserNotFoundException;

/**
 * Defines methods used to manage user permissions within Acquire, such as
 * assigning programs, sites, roles, and functions. Referenced in EL as
 * "acquirePermissionManager" to distinguish between this class and Seam
 * Security classes.
 * 
 * Programs are broad groups specifying a set of collaborators on a specific
 * project or grant.
 * 
 * Sites are specific groups of researchers or institutions that belong to
 * one or more programs. Sites can also be children of other sites if there
 * are subdivisions within an institution.
 * 
 * Roles define permissions to view different data points, such as PHI or 
 * non-protected. They are assigned per Site.
 * 
 * Program Roles define permissions that apply to all sites within a program,
 * such as Admin or Public Researcher. They are assigned per Program.
 * 
 * Functions provide permissions to view specific functionality within a 
 * program. They are assigned per Program.
 * 
 */
@SessionScoped
@Local
public interface PermissionManager
{
  /**
   * Provides a list of programs that the current user is assigned to.
   * 
   * @return the List&lt;String&gt; containing the names of all programs that
   *         the current user belongs to.
   * @throws IdentityException
   */
  List<String> getUserPrograms() throws IdentityException;

  /**
   * Sets the name of the program the user is currently working in.
   * 
   * @param program
   */
  void setUserCurrentProgram(String program);

  /**
   * Gets the name of the user's current program.
   * 
   * @return the name of the curren't user's active program.
   */
  String getUserCurrentProgram();

  /**
   * Provides a list of all programs currently registered in Acquire.
   * 
   * @return the List&lt;String&gt; containing the names of all programs in
   *         Acquire
   * @throws IdentityException
   */
  List<String> getAllPrograms() throws IdentityException;

  /**
   * Provides a list of all Sites currently registered in Acquire.
   * 
   * @return the List&lt;String&gt; containing the names of all sites in Acquire
   * @throws IdentityException
   */
  List<String> getAllSites() throws IdentityException;

  /**
   * Provides a list of all sites assigned to the current program
   * 
   * @return the List&lt;String&gt; containing the names of all sites in the
   *         current program
   * @throws IdentityException
   */
  List<String> getProgramSites() throws IdentityException;

  /**
   * Provides a list of Sites the current user has permissions for in the user's
   * current program.
   * 
   * @return the List&lt;String&gt; containing the names of the current user's
   *         sites in the user's current program.
   * @throws IdentityException
   */
  List<String> getUserProgramSites() throws IdentityException;

  /**
   * Getter for the user privileges are currently being assigned to.
   * 
   * @return the username of the user privileges are currently being assigned
   *         to.
   */
  AcquireUserInformation getUser();

  /**
   * Setter for the user privileges are currently being assigned to. Calling
   * this method will clear out other values.
   * 
   * @param username
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   */
  void setUser(AcquireUserInformation username) throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Getter for the current Site
   * 
   * @return
   */
  String getSite();

  /**
   * Setter for the current Site
   * 
   * @param sitename
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   */
  void setSite(String sitename) throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Getter for the current Program
   * 
   * @return
   */
  String getProgram();

  /**
   * Setter for the current Program
   * 
   * @param programname
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   */
  void setProgram(String programname) throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Provides the list of Acquire roles available
   * 
   * @return
   */
  List<String> getRoleChoices();

  /**
   * Provides the list of Acquire UI Functions available
   * 
   * @return
   */
  List<String> getFunctionChoices();

  /**
   * Provides the list of program roles
   */
  List<String> getProgramRoleChoices();

  /**
   * Getter for the current role
   * 
   * @return
   */
  String getRole();

  /**
   * Setter for the current role
   * 
   * @param roleName
   */
  void setRole(String roleName);

  /**
   * getter for the current Function
   * 
   * @return
   */
  List<String> getFunctions();

  /**
   * setter for the current Function
   * 
   * @param functionName
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   */
  void setFunctions(List<String> functionName) throws IdentityException,
      FeatureNotSupportedException;

  /**
   * This method adds the currently selected role to the currently selected user
   * for the currently selected Site. This will replace the current values for
   * the user
   * 
   * 
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   * @throws AddRelationshipException
   * @throws UserNotFoundException
   */
  void addRole() throws IdentityException, FeatureNotSupportedException,
      UserNotFoundException, AddRelationshipException;

  /**
   * This method adds the currently selected function to the currently selected
   * user for the currently selected Program.
   * 
   * 
   * @throws FeatureNotSupportedException
   * @throws IdentityException
   */
  void updateFunctions() throws IdentityException, FeatureNotSupportedException;

  /**
   * Adds the currently selected site to the currently selected program
   * 
   * @throws IdentityException
   */
  void addSiteToProgram() throws IdentityException;

  /**
   * Method to check if a site is associated with a given program.
   * 
   * @param site
   * @param program
   * @return
   * @throws IdentityException
   */
  boolean isSiteInProgram(String site, String program) throws IdentityException;

  /**
   * Method to remove a role from the user
   * 
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  void removeRole() throws IdentityException, FeatureNotSupportedException;

  /**
   * Method that removes all roles from the user
   */
  void clearRoles() throws IdentityException, FeatureNotSupportedException;

  /**
   * Method that removes all Program Roles from the user
   */
  void clearProgramRoles() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Method that removes all Functions from the user
   */
  void clearFunctions() throws IdentityException, FeatureNotSupportedException;

  /**
   * Method that adds a program role to the user. This will replace previously
   * set values
   */
  void addProgramRole() throws IdentityException, FeatureNotSupportedException;

  /**
   * Method that removes a program role from the user.
   */
  void removeProgramRole() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Sets the current programRole
   * 
   * @param programRole
   */
  void setProgramRole(String programRole);

  /**
   * Gets the current programRole
   * 
   * @return
   */
  String getProgramRole();

  /**
   * List the roles for the current site.
   * 
   * @return
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  List<String> listRoles() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Lists the functions for the current program.
   * @return
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  List<String> listFunctions() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Lists the program roles for the current program.
   * @return
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  List<String> listProgramRoles() throws IdentityException,
      FeatureNotSupportedException;

  /**
   * Lists the current users's sites in the current program.
   * @return
   * @throws IdentityException
   * @throws FeatureNotSupportedException
   */
  List<String> getCurrentSites() throws IdentityException,
      FeatureNotSupportedException;


}
