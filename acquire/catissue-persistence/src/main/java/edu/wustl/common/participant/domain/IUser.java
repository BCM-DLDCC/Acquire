
package edu.wustl.common.participant.domain;


/**
 * The Interface IUser.
 */

public interface IUser
{

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	Long getId();

	/**
	 * Sets the id.
	 *
	 * @param identifier the new id
	 */
	void setId(Long identifier);

	/**
	 * Gets the role id.
	 *
	 * @return the role id
	 */
	String getRoleId();

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	String getFirstName();

	/**
	 * Sets the first name.
	 *
	 * @param firstName the new first name
	 */
	void setFirstName(String firstName);

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	String getLastName();

	/**
	 * Sets the last name.
	 *
	 * @param lastName the new last name
	 */
	void setLastName(String lastName);

	/**
	 * Gets the csm user id.
	 *
	 * @return the csm user id
	 */
	Long getCsmUserId();

	/**
	 * Sets the csm user id.
	 *
	 * @param csmUserId the new csm user id
	 */
	void setCsmUserId(Long csmUserId);

	/**
	 * Gets the login name.
	 *
	 * @return loginName.
	 */
	String getLoginName();

	/**
	 * Sets the login name.
	 *
	 * @param loginName :loginName.
	 */
	void setLoginName(String loginName);

	/**
	 * Gets the adminuser.
	 *
	 * @return adminuser.
	 */
	Boolean getAdminuser();

	/**
	 * Sets the adminuser.
	 *
	 * @param adminuser the adminuser
	 */
	void setAdminuser(Boolean adminuser);
}
