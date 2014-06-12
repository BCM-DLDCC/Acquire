
package edu.wustl.common.participant.domain;


/**
 * The Interface ISite.
 */
public interface ISite
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	void setName(String name);

	/**
	 * Gets the facility id.
	 *
	 * @return the facility id
	 */
	String getFacilityId();

	/**
	 * Sets the facility id.
	 *
	 * @param facilityId the new facility id
	 */
	void setFacilityId(String facilityId);
}
