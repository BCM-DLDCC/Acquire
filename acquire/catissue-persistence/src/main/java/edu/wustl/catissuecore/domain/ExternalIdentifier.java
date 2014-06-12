/**
 * <p>Title: ExternalIdentifier Class>
 * <p>Description: A pre-existing, externally defined
 * id associated with a specimen.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.catissuecore.domain;

import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.exception.AssignDataException;

/**
 * A pre-existing, externally defined
 * id associated with a specimen.
 * @hibernate.class table="CATISSUE_EXTERNAL_IDENTIFIER"
 * @author gautam_shetty
 */

public class ExternalIdentifier extends AbstractDomainObject
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
	 * Name of the legacy id.
	 */
	protected String name;

	/**
	 * Value of the legacy id.
	 */
	protected String value;

	/**
	 * Specimen.
	 */
	protected Specimen specimen;

	/**
	 * Returns the system generated unique id.
	 * @hibernate.id name="id" column="IDENTIFIER" type="long" length="30"
	 * unsaved-value="null" generator-class="native"
	 * @hibernate.generator-param name="sequence" value="CATISSUE_EXTERNAL_ID_SEQ"
	 * @return the system generated unique id.
	 * @see #setId(Long)
	 * */
	@Override
	public Long getId()
	{
		return this.id;
	}

	/**
	 * Sets the system generated unique id.
	 * @param identifier the system generated unique id.
	 * @see #getId()
	 * */
	@Override
	public void setId(Long identifier)
	{
		this.id = identifier;
	}

	/**
	 * Returns the name of the legacy id.
	 * @hibernate.property name="name" type="string"
	 * column="NAME" length="255"
	 * @return the name of the legacy id.
	 * @see #setName(String)
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Sets the name of the legacy id.
	 * @param name the name of the legacy id.
	 * @see #getName()
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the value of the legacy id.
	 * @hibernate.property name="value" type="string"
	 * column="VALUE" length="255"
	 * @return the value of the legacy id.
	 * @see #setValue(String)
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * Sets the value of the legacy id.
	 * @param value the value of the legacy id.
	 * @see #getValue()
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @hibernate.many-to-one column="SPECIMEN_ID" class="edu.wustl.catissuecore.
	 * domain.Specimen" constrained="true"
	 * @see #setRegistration(Site)
	 * @return Specimen
	 */
	public Specimen getSpecimen()
	{
		return this.specimen;
	}

	/**
	 * @param specimen The specimen to set.
	 */
	public void setSpecimen(Specimen specimen)
	{
		this.specimen = specimen;
	}

	/* (non-Javadoc)
	 * @see edu.wustl.catissuecore.domain.AbstractDomainObject#setAllValues(edu.
	 * wustl.catissuecore.actionForm.AbstractActionForm)
	 */
	/**
	 * Set All Values.
	 * @param abstractForm IValueObject
	 * @throws AssignDataException AssignDataException.
	 */
	@Override
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		//
	}

	/**
	 * To String Method.
	 * @return String.
	 */
	@Override
	public String toString()
	{
		return "EI{" + "id " + this.id + "\t" + "Name " + this.name + "\t" + "Value " + this.value
				+ "}";
	}

	/**
	 * Default Constructor.
	 */
	public ExternalIdentifier()
	{
		super();
	}

	/**
	 * Parameterized Constructor.
	 * @param externalIdentifier ExternalIdentifier.
	 */
	public ExternalIdentifier(ExternalIdentifier externalIdentifier)
	{
		super();
		this.name = externalIdentifier.getName();
		this.value = externalIdentifier.getValue();
	}
}