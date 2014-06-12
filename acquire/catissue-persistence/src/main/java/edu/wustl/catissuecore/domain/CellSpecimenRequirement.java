/**
 * <p>Title: CellSpecimen Class>
 * <p>Description:  A biospecimen composed of purified single cells not in the
 * context of a tissue or other biospecimen fluid.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author virender_mehta
 * @version catissueSuite V1.1
 */

package edu.wustl.catissuecore.domain;

import java.io.Serializable;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;

/**
 * A biospecimen composed of purified single cells not in the
 * context of a tissue or other biospecimen fluid.
 * @hibernate.subclass name="CellSpecimenRequirement" discriminator-value = "Cell"
 */
public class CellSpecimenRequirement extends SpecimenRequirement implements Serializable
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static final Logger logger = Logger.getCommonLogger(CellSpecimenRequirement.class);

	/**
	 * Serial Version Id of the class.
	 */
	private static final long serialVersionUID = 1232228923230L;

	/**
	 * Default Constructor.
	 */
	public CellSpecimenRequirement()
	{
		super();
	}

	/**
	 * Parameterized Constructor.
	 * @param form AbstractActionForm.
	 * @throws AssignDataException : AssignDataException
	 */
	public CellSpecimenRequirement(AbstractActionForm form) throws AssignDataException
	{
		super();
		this.setAllValues(form);
	}

	/**
	 * This function Copies the data from an NewSpecimenForm object to a CellSpecimen object.
	 * @param abstractForm - siteForm An SiteForm object containing the information about the site.
	 * @throws AssignDataException : AssignDataException
	 * */
	@Override
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		try
		{
			super.setAllValues(abstractForm);
		}
		catch (final Exception excp)
		{
			CellSpecimenRequirement.logger.error(excp.getMessage(), excp);
			excp.printStackTrace();
			final ErrorKey errorKey = ErrorKey.getErrorKey("assign.data.error");
			throw new AssignDataException(errorKey, null, "CellSpecimenRequirment.java :");
		}
	}

	/**
	 * Parameterized Constructor.
	 * @param cellRequirementSpecimen of type CellSpecimenRequirement class.
	 */
	public CellSpecimenRequirement(CellSpecimenRequirement cellRequirementSpecimen)
	{
		super();
	}

	/**
	 * Method to create a clone object of CellSpecimenRequirement type.
	 * @return CellSpecimenRequirement object.
	 */
	public CellSpecimenRequirement createClone()
	{
		final CellSpecimenRequirement cloneCellRequirementSpecimen = new CellSpecimenRequirement(
				this);
		return cloneCellRequirementSpecimen;
	}
}