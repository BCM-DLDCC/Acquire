/**
 * <p>Title: FluidSpecimenReviewEventParametersForm Class</p>
 * <p>Description:  This Class handles the Fluid Specimen Review event parameters.
 * <p> It extends the EventParametersForm class.    
 * Copyright:    Copyright (c) 2005
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Mandar Deshmukh
 * @version 1.00
 * Created on July 28th, 2005
 */

package edu.wustl.catissuecore.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.domain.FluidSpecimenReviewEventParameters;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author mandar_deshmukh
 *   This Class handles the Fluid Specimen Review event parameters.
 */
public class FluidSpecimenReviewEventParametersForm extends SpecimenEventParametersForm
{

	private static final long serialVersionUID = 1L;

	/**
	 * logger Logger - Generic logger.
	 */
	private static Logger logger = Logger.getCommonLogger(FluidSpecimenReviewEventParametersForm.class);
	/**
	 * Cell Count.
	 */
	protected String cellCount;

	/**
	 * Returns the cell count. 
	 * @return The cell count.
	 * @see #setCellCount(double)
	 */
	public String getCellCount()
	{
		return this.cellCount;
	}

	/**
	 * Sets the cell count.
	 * @param cellCount the cell count.
	 * @see #getCellCount()
	 */
	public void setCellCount(String cellCount)
	{
		this.cellCount = cellCount;
	}

	//	 ----- SUPERCLASS METHODS
	/**
	 * @see edu.wustl.catissuecore.actionForm.AbstractActionForm#getFormId()
	 * @return FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS_FORM_ID
	 */
	@Override
	public int getFormId()
	{
		return Constants.FLUID_SPECIMEN_REVIEW_EVENT_PARAMETERS_FORM_ID;
	}

	/**
	 * @see edu.wustl.catissuecore.actionForm.AbstractActionForm#setAllValues(edu.wustl.catissuecore.domain.AbstractDomainObject)
	 * @param abstractDomain An AbstractDomainObject obj
	 */
	@Override
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		super.setAllValues(abstractDomain);
		final FluidSpecimenReviewEventParameters fluidSpecimenReviewEventParametersObject = (FluidSpecimenReviewEventParameters) abstractDomain;
		this.cellCount = CommonUtilities.toString(fluidSpecimenReviewEventParametersObject
				.getCellCount());
		logger.debug("############FormObject################## : ");
		logger.debug(this.cellCount);
		logger.debug("############################## : ");
	}

	/**
	 * Overrides the validate method of ActionForm.
	 * @return error ActionErrors instance
	 * @param mapping Actionmapping instance
	 * @param request HttpServletRequest instance
	 */
	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
	{
		final ActionErrors errors = super.validate(mapping, request);
		final Validator validator = new Validator();

		try
		{
			//         // checks the cellCount
			if (!Validator.isEmpty(this.cellCount) && !validator.isDouble(this.cellCount, false))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.format",
						ApplicationProperties
								.getValue("fluidspecimenrevieweventparameters.cellcount")));
			}
		}
		catch (final Exception excp)
		{
			FluidSpecimenReviewEventParametersForm.logger.error(excp.getMessage(),excp);
			excp.printStackTrace();
		}
		return errors;
	}

	/**
	 * Resets the values of all the fields.
	 * This method defined in ActionForm is overridden in this class.
	 */

	@Override
	protected void reset()
	{
		//         super.reset();
		//  this.cellCount = null;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}

}
