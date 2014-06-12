/**
 * <p>Title: ParticipantMedicalIdentifier Class>
 * <p>Description:  A medical record identification inforatioin that refers to a Participant. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 */

package edu.wustl.catissuecore.domain;

import edu.wustl.catissuecore.util.SearchUtil;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.participant.domain.IParticipantMedicalIdentifier;

/**
 * A medical record identification number that refers to a Participant.
 * @hibernate.class table="CATISSUE_PART_MEDICAL_ID"
 */
public class ParticipantMedicalIdentifier extends AbstractDomainObject
		implements
			java.io.Serializable, IParticipantMedicalIdentifier<Participant, Site>
{

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 1234567890L;

	/**
	 * System generated unique id.
	 */
	protected Long id;

	/**
	 * Participant's medical record number used in their medical treatment.
	 */
	protected String medicalRecordNumber;

	/**
	 * Source of medical record number.
	 */
	protected Site site;

	/**
	 * Participant.
	 */
	protected Participant participant;

	/**
	 * Default constructor.
	 */
	public ParticipantMedicalIdentifier()
	{
		super();
	}

	/**
	 * Copy Constructor.
	 * @param pmi ParticipantMedicalIdentifier Object
	 */
	public ParticipantMedicalIdentifier(ParticipantMedicalIdentifier pmi)
	{
		super();
		this.id = Long.valueOf(pmi.getId().longValue());
		this.medicalRecordNumber = pmi.getMedicalRecordNumber();
		if (pmi.getSite() != null)
		{
			this.site = new Site(pmi.getSite());
		}
	}

	/**
	 * Returns System generated unique identifier.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_PART_MEDICAL_ID_SEQ"
	 * @return System generated unique id.
	 * @see #setId(Long)
	 */
	@Override
	public Long getId()
	{
		return this.id;
	}

	/**
	 * Sets a system id for a particular medical record.
	 * @param identifier id for a particular medical record.
	 * @see #getId()
	 * */
	@Override
	public void setId(Long identifier)
	{
		this.id = identifier;
	}

	/**
	 * Returns the Medical Record Number.
	 * @hibernate.property name="medicalRecordNumber" type="string"
	 * column="MEDICAL_RECORD_NUMBER" length="50"
	 * @return the Medical Record Number.
	 * @see #setMedicalRecordNumber(String)
	 */
	public String getMedicalRecordNumber()
	{
		return this.medicalRecordNumber;
	}

	/**
	 * Sets a Medical Record Number.
	 * @param medicalRecordNumber Medical Record Number.
	 * @see #getMedicalRecordNumber()
	 */
	public void setMedicalRecordNumber(String medicalRecordNumber)
	{
		this.medicalRecordNumber = medicalRecordNumber;
	}

	/**
	 * Returns the source of medical record number.
	 * @hibernate.many-to-one column="SITE_ID"  class="edu.wustl.catissuecore.domain.Site" constrained="true"
	 * @return the source of medical record number.
	 * @see #setSite(Site)
	 */
	public Site getSite()
	{
		return this.site;
	}

	/**
	 * Returns the source of medical record number.
	 * @see #setSite(Site)
	 * @param site Site.
	 */
	public void setSite(Site site)
	{
		this.site = site;
	}

	/**
	 * @hibernate.many-to-one column="PARTICIPANT_ID"  class="edu.wustl.catissuecore.
	 * domain.Participant" constrained="true"
	 * @see #setParticipant(Site)
	 * @return Participant.
	 */
	public Participant getParticipant()
	{
		return this.participant;
	}

	/**
	 * @param participant The participant to set.
	 */
	public void setParticipant(Participant participant)
	{
		this.participant = participant;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.catissuecore.domain.AbstractDomainObject#setAllValues(
	 * edu.wustl.catissuecore.actionForm.AbstractActionForm)
	 */
	/**
	 * Set All Values.
	 * @param abstractForm IValueObject.
	 * @throws AssignDataException AssignDataException.
	 */
	@Override
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		//Change for API Search   --- Ashwin 04/10/2006
		if (SearchUtil.isNullobject(this.site))
		{
			this.site = new Site();
		}
	}

	/**
	 * To String.
	 * @return String.
	 */
	@Override
	public String toString()
	{
		return "ParticipantMedicalIdentifier: " + this.id + ", " + this.medicalRecordNumber + ", "
				+ this.site;
	}
}