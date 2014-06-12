
package edu.wustl.common.participant.domain;

/**
 * The Interface IParticipantMedicalIdentifier.
 */
public interface IParticipantMedicalIdentifier<T, S>
{

	/**
	 * Sets the id.
	 *
	 * @param identifier the new id
	 */
	void setId(Long identifier);

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	Long getId();

	/**
	 * Gets the medical record number.
	 *
	 * @return the medical record number
	 */
	String getMedicalRecordNumber();

	/**
	 * Sets the medical record number.
	 *
	 * @param medicalRecordNumber the new medical record number
	 */
	void setMedicalRecordNumber(String medicalRecordNumber);

	/**
	 * Sets the participant.
	 *
	 * @param participant the new participant
	 */
	void setParticipant(T participant);

	/**
	 * Gets the site.
	 *
	 * @return the site
	 */
	S getSite();

	/**
	 * Sets the site.
	 *
	 * @param site the new site
	 */
	void setSite(S site);
}
