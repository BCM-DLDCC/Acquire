/**
 * <p>Title: DisposalEventParametersForm Class</p>
 * <p>Description:  This Class handles the disposal event parameters..
 * <p> It extends the EventParametersForm class.    
 * Copyright:    Copyright (c) 2005
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Jyoti Singh
 * @version 1.00
 * Created on July 28th, 2005
 */

package edu.wustl.catissuecore.actionForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.domain.DisposalEventParameters;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.logger.Logger;

/**
 * @author Jyoti_Singh
 *
 * Description:  This Class handles the Disposal event parameters..
 */
public class DisposalEventParametersForm extends SpecimenEventParametersForm
{

	private static final long serialVersionUID = 1L;

	/**
	 * logger Logger - Generic logger.
	 */
	private static Logger logger = Logger.getCommonLogger(DisposalEventParametersForm.class);

	/**
	 * reason for disposal of specimen it.
	 */
	private String reason;

	/**
	 * Activity Status of the Specimen
	 */
	private String activityStatus;

	/**
	 * Getting Activity status
	 * @return activityStatus 
	 */
	@Override
	public String getActivityStatus()
	{
		return this.activityStatus;
	}

	/**
	 * @param activityStatus Setting Activity Status
	 */
	@Override
	public void setActivityStatus(final String activityStatus)
	{
		this.activityStatus = activityStatus;
	}

	/**
	 * @return Returns the reason applied on specimen for its Disposal.
	 */
	public String getReason()
	{
		return this.reason;
	}

	/**
	 * @param reason The reason to set.
	 */
	public void setReason(final String reason)
	{
		this.reason = reason;
	}

	/** 
	 * @see edu.wustl.catissuecore.actionForm.AbstractActionForm#getFormId()
	 * @return DISPOSAL_EVENT_PARAMETERS_FORM_ID
	 */
	@Override
	public int getFormId()
	{
		return Constants.DISPOSAL_EVENT_PARAMETERS_FORM_ID;
	}

	/**
	 * @see edu.wustl.catissuecore.actionForm.AbstractActionForm#setAllValues(edu.wustl.catissuecore.domain.AbstractDomainObject)
	 * @param abstractDomain An AbstractDomainObject Obj
	 */
	@Override
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		super.setAllValues(abstractDomain);
		final DisposalEventParameters disEvtPar = (DisposalEventParameters) abstractDomain;
		this.reason = CommonUtilities.toString(disEvtPar.getReason());
		this.activityStatus = disEvtPar.getSpecimen().getActivityStatus();
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

		try
		{
			//resolved bug# 4058	
			if (!this.activityStatus.equalsIgnoreCase(Constants.ACTIVITY_STATUS_VALUES[2])
					&& !this.activityStatus.equalsIgnoreCase(Constants.ACTIVITY_STATUS_VALUES[3]))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.selected",
						ApplicationProperties.getValue("disposaleventparameters.activityStatus")));
			}
			//Commented due to Bug:- 3106: No need to have Disposal Reason a required field
			// checks the reason
			//           	if (validator.isEmpty(reason ) )
			//            {
			//           		errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",ApplicationProperties.getValue("disposaleventparameters.reason")));
			//            }
		}
		catch (final Exception excp)
		{
			DisposalEventParametersForm.logger.error(excp.getMessage(),excp);
			excp.printStackTrace() ;
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
		//        super.reset();
		//        this.reason = null;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}

}