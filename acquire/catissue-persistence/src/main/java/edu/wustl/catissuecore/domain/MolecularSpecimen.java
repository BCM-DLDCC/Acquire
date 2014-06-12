/**
 * <p>Title: MolecularSpecimen Class>
 * <p>Description:  A molecular derivative (I.e. RNA / DNA / Protein Lysate)
 * obtained from a specimen.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Gautam Shetty
 * @version 1.00
 */

package edu.wustl.catissuecore.domain;

import java.io.Serializable;

import edu.wustl.catissuecore.actionForm.SpecimenForm;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.actionForm.IValueObject;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.logger.Logger;

/**
 * A molecular derivative (I.e. RNA / DNA / Protein Lysate) obtained from a specimen.
 * @hibernate.subclass name="MolecularSpecimen" discriminator-value="Molecular"
 */
public class MolecularSpecimen extends Specimen implements Serializable
{

	/**
	 * logger Logger - Generic logger.
	 */
	private static Logger logger = Logger.getCommonLogger(MolecularSpecimen.class);

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 1234567890L;

	/**
	 * Concentration of liquid molecular specimen measured in microgram per microlitter.
	 */
	protected Double concentrationInMicrogramPerMicroliter;

	/**
	 * Default Constructor.
	 */
	public MolecularSpecimen()
	{
		super();
	}

	/**
	 * Parameterized Constructor.
	 * @param form AbstractActionForm.
	 * @throws AssignDataException : AssignDataException
	 */
	public MolecularSpecimen(AbstractActionForm form) throws AssignDataException
	{
		super();
		this.setAllValues(form);
	}

	/**
	 * Returns the concentration of liquid molecular specimen measured
	 * in microgram per microlitter.
	 * @hibernate.property name="concentrationInMicrogramPerMicroliter" type="double"
	 * column="CONCENTRATION" length="50"
	 * @return the concentration of liquid molecular specimen measured
	 * in microgram per microlitter.
	 * directly collected from participant or created from another specimen.
	 * @see #setConcentrationInMicrogramPerMicroLiter(Double)
	 */
	public Double getConcentrationInMicrogramPerMicroliter()
	{
		return this.concentrationInMicrogramPerMicroliter;
	}

	/**
	 * Sets the concentration of liquid molecular specimen measured
	 * in microgram per microlitter.
	 * @param concentrationInMicrogramPerMicroliter the concentration of
	 * liquid molecular specimen measuredin microgram per microlitter.
	 * @see #getConcentrationInMicrogramPerMicroLiter()
	 */
	public void setConcentrationInMicrogramPerMicroliter(
			Double concentrationInMicrogramPerMicroliter)
	{
		this.concentrationInMicrogramPerMicroliter = concentrationInMicrogramPerMicroliter;
	}

	/**
	 * This function Copies the data from an NewSpecimenForm object to a MolecularSpecimen object.
	 * @param abstractForm An SiteForm object containing the information about the site.
	 * @throws AssignDataException : AssignDataException
	 * */
	@Override
	public void setAllValues(IValueObject abstractForm) throws AssignDataException
	{
		try
		{
			super.setAllValues(abstractForm);
			final SpecimenForm form = (SpecimenForm) abstractForm;
			if (Constants.DOUBLE_QUOTES.equals(form.getConcentration()))
			{
				MolecularSpecimen.logger.debug("Concentration is " + form.getConcentration());
			}
			else
			{
				this.concentrationInMicrogramPerMicroliter = new Double(form.getConcentration());
			}
		}
		catch (final Exception excp)
		{
			MolecularSpecimen.logger.error(excp.getMessage(),excp);
			excp.printStackTrace();
			final ErrorKey errorKey = ErrorKey.getErrorKey("assign.data.error");
			throw new AssignDataException(errorKey, null, "MolecularSpecimen.java :");
		}
	}

	/**
	 * Parameterized Constructor.
	 * @param molecularReqSpecimen SpecimenRequirement.
	 */
	public MolecularSpecimen(SpecimenRequirement molecularReqSpecimen)
	{
		super(molecularReqSpecimen);
		this.concentrationInMicrogramPerMicroliter = ((MolecularSpecimenRequirement) molecularReqSpecimen).concentrationInMicrogramPerMicroliter;
	}
}