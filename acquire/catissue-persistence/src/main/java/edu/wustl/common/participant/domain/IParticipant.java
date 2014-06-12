
package edu.wustl.common.participant.domain;

import java.util.Collection;
import java.util.Date;

import edu.wustl.catissuecore.domain.Race;

/**
 * The Interface IParticipant.
 */
public interface IParticipant
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
	 * Gets the middle name.
	 *
	 * @return the middle name
	 */
	String getMiddleName();

	/**
	 * Sets the middle name.
	 *
	 * @param middleName the new middle name
	 */
	void setMiddleName(String middleName);

	/**
	 * Gets the birth date.
	 *
	 * @return the birth date
	 */
	Date getBirthDate();

	/**
	 * Sets the birth date.
	 *
	 * @param birthDate the new birth date
	 */
	void setBirthDate(Date birthDate);

	/**
	 * Gets the gender.
	 *
	 * @return the gender
	 */
	String getGender();

	/**
	 * Sets the gender.
	 *
	 * @param gender the new gender
	 */
	void setGender(String gender);

	/**
	 * Gets the sex genotype.
	 *
	 * @return the sex genotype
	 */
	String getSexGenotype();

	/**
	 * Sets the sex genotype.
	 *
	 * @param sexGenotype the new sex genotype
	 */
	void setSexGenotype(String sexGenotype);

	/**
	 * Gets the race collection.
	 *
	 * @return the race collection
	 */
	Collection<Race> getRaceCollection();

	/**
	 * Sets the race collection.
	 *
	 * @param raceCollection the new race collection
	 */
	public void setRaceCollection(Collection<Race> raceCollection);

	/**
	 * Gets the ethnicity.
	 *
	 * @return the ethnicity
	 */
	String getEthnicity();

	/**
	 * Sets the ethnicity.
	 *
	 * @param ethnicity the new ethnicity
	 */
	void setEthnicity(String ethnicity);

	/**
	 * Gets the social security number.
	 *
	 * @return the social security number
	 */
	String getSocialSecurityNumber();

	/**
	 * Sets the social security number.
	 *
	 * @param socialSecurityNumber the new social security number
	 */
	void setSocialSecurityNumber(String socialSecurityNumber);

	/**
	 * Gets the activity status.
	 *
	 * @return the activity status
	 */
	String getActivityStatus();

	/**
	 * Sets the activity status.
	 *
	 * @param activityStatus the new activity status
	 */
	void setActivityStatus(String activityStatus);

	/**
	 * Gets the death date.
	 *
	 * @return the death date
	 */
	Date getDeathDate();

	/**
	 * Sets the death date.
	 *
	 * @param deathDate the new death date
	 */
	void setDeathDate(Date deathDate);

	/**
	 * Gets the vital status.
	 *
	 * @return the vital status
	 */
	String getVitalStatus();

	/**
	 * Sets the vital status.
	 *
	 * @param vitalStatus the new vital status
	 */
	void setVitalStatus(String vitalStatus);

	/**
	 * Gets the participant medical identifier collection.
	 *
	 * @return the participant medical identifier collection
	 */
	Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> getParticipantMedicalIdentifierCollection();

	/**
	 * Sets the participant medical identifier collection.
	 *
	 * @param participantMedicalIdentifierCollection the new participant medical identifier collection
	 */
	void setParticipantMedicalIdentifierCollection(
			Collection<IParticipantMedicalIdentifier<IParticipant, ISite>> participantMedicalIdentifierCollection);

	/**
	 * Gets the meta phone code.
	 *
	 * @return the meta phone code
	 */
	String getMetaPhoneCode();

	/**
	 * Sets the meta phone code.
	 *
	 * @param metaPhoneCode the new meta phone code
	 */
	void setMetaPhoneCode(String metaPhoneCode);



}