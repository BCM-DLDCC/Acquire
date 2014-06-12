
package edu.wustl.common.participant.actionForm;

import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * @author geeta_jaggal
 * The Interface IParticipantForm.
 */
public interface IParticipantForm
{

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	String getLastName();

	/**
	 * Sets the last name.
	 *
	 * @param s the new last name
	 */
	void setLastName(String lastName);

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	String getFirstName();

	/**
	 * Sets the first name.
	 *
	 * @param s the new first name
	 */
	void setFirstName(String firstName);

	/**
	 * Gets the birth date.
	 *
	 * @return the birth date
	 */
	String getBirthDate();

	/**
	 * Sets the birth date.
	 *
	 * @param s the new birth date
	 */
	void setBirthDate(String birthDate);

	/**
	 * Sets the operation.
	 *
	 * @param s the new operation
	 */
	void setOperation(String operation);

	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 */
	String getOperation();

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	long getId();

	/**
	 * Sets the id.
	 *
	 * @param identifier the new id
	 */
	void setId(long identifier);


	/**
	 * @return cpId
	 */
	long getCpId();

	/**
	 * @param cpId Set cpId
	 */
	void setCpId(long cpId);

	public void setCollectionProtocolRegistrationValues(Map colProtoRegnVals);

	public Map getCollectionProtocolRegistrationValues();

	public String getSocialSecurityNumberPartA();

	public String getSocialSecurityNumberPartB();

	public String getSocialSecurityNumberPartC();

	public Map getValues();
}
