/**
 * <p>Title: ProcedureEventParametersForm Class</p>
 * <p>Description:  This Class handles the Procedure event parameters..
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

import edu.wustl.catissuecore.domain.ProcedureEventParameters;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.CommonUtilities;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * @author Jyoti_Singh
 *
 * Description:  This Class handles the Procedure event parameters..
 */
public class ProcedureEventParametersForm extends SpecimenEventParametersForm
{

	private static final long serialVersionUID = 1L;

	/**
	 * logger Logger - Generic logger.
	 */
	private static Logger logger = Logger.getCommonLogger(ProcedureEventParametersForm.class);
	private String url;
	private String name;

	/**
	 * @return name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name Setting name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return url
	 */
	public String getUrl()
	{
		return this.url;
	}

	/**
	 * @param url Setting url
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * @see edu.wustl.catissuecore.actionForm.AbstractActionForm#getFormId()
	 * @return PROCEDURE_EVENT_PARAMETERS_FORM_ID
	 */
	@Override
	public int getFormId()
	{
		return Constants.PROCEDURE_EVENT_PARAMETERS_FORM_ID;
	}

	/**
	 * @see edu.wustl.catissuecore.actionForm.AbstractActionForm#setAllValues(edu.wustl.catissuecore.domain.AbstractDomainObject)
	 * @param abstractDomain An AbstractDomain Object  
	 */
	@Override
	public void setAllValues(AbstractDomainObject abstractDomain)
	{
		super.setAllValues(abstractDomain);
		final ProcedureEventParameters procedureEventParametersObject = (ProcedureEventParameters) abstractDomain;
		this.url = CommonUtilities.toString(procedureEventParametersObject.getUrl());
		this.name = CommonUtilities.toString(procedureEventParametersObject.getName());
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
			// Mandar 10-apr-06 : bugid :353 
			// Error messages should be in the same sequence as the sequence of fields on the page.
			if (Validator.isEmpty(this.url))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("procedureeventparameters.url")));
			}

			if (Validator.isEmpty(this.name))
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
						ApplicationProperties.getValue("procedureeventparameters.name")));
			}

		}
		catch (final Exception excp)
		{
			ProcedureEventParametersForm.logger.error(excp.getMessage(),excp);
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
		//        super.reset();
		//		this.url = null;
		//		this.name = null;
	}

	@Override
	public void setAddNewObjectIdentifier(String arg0, Long arg1)
	{
		// TODO Auto-generated method stub

	}

}