/**
 * <p>Title: QuickEventsSpecimenSearchAction Class</p>
 * <p>Description:  This class validates the entries from the
 *  Quickevents webpage and directs the flow accordingly. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Mandar Deshmukh
 * @version 1.00
 * Created on July 3, 2006
 */

package edu.wustl.catissuecore.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.QuickEventsForm;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.global.Validator;
import edu.wustl.common.util.logger.Logger;

/**
 * <p>Title: QuickEventsSpecimenSearchAction Class</p>
 * <p>Description:  This class validates the entries from the
 *  Quickevents webpage and directs the flow accordingly. </p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Mandar Deshmukh
 * @version 1.00
 * Created on July 3, 2006
 */

public class QuickEventsSpecimenSearchAction extends BaseAction
{

	/**
	 * Overrides the executeSecureAction method of SecureAction class.
	 * @param mapping
	 *            object of ActionMapping
	 * @param form
	 *            object of ActionForm
	 * @param request
	 *            object of HttpServletRequest
	 * @param response
	 *            object of HttpServletResponse
	 * @throws Exception
	 *             generic exception
	 * @return ActionForward : ActionForward
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		final QuickEventsForm qEForm = (QuickEventsForm) form;
		Logger.out.debug(qEForm.getSpecimenEventParameter());
		String pageOf = Constants.SUCCESS;

		pageOf = this.validate(request, qEForm);

		if (pageOf.equals(Constants.SUCCESS))
		{
			//DefaultBizLogic bizLogic = BizLogicFactory.getDefaultBizLogic();
			final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			final IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
			String specimenFound = "0";
			String errorString = "";
			String specimenLabel = "";
			if (qEForm.getCheckedButton().equals("1"))
			{
				specimenLabel = qEForm.getSpecimenLabel();
				specimenFound = this.isExistingSpecimen(Constants.SYSTEM_LABEL, specimenLabel,
						bizLogic);
				errorString = ApplicationProperties.getValue("quickEvents.specimenLabel");
			}
			else if (qEForm.getCheckedButton().equals("2"))
			{
				final String barCode = qEForm.getBarCode();
				specimenFound = this
						.isExistingSpecimen(Constants.SYSTEM_BARCODE, barCode, bizLogic);
				errorString = ApplicationProperties.getValue("quickEvents.barcode");
			}

			if (!specimenFound.equalsIgnoreCase("0"))
			{
				request.setAttribute(Constants.SPECIMEN_ID, specimenFound);
				request.setAttribute(Constants.SPECIMEN_LABEL, specimenLabel);
				final String selectedEvent = qEForm.getSpecimenEventParameter();
				request.setAttribute(Constants.EVENT_SELECTED, selectedEvent);

				request.setAttribute("isQuickEvent", "true");
				pageOf = Constants.SUCCESS;
			}
			else
			{
				ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
				if (errors == null)
				{
					errors = new ActionErrors();
				}
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"quickEvents.specimen.notExists", errorString));
				this.saveErrors(request, errors);

				pageOf = Constants.FAILURE;
			}
		}

		return mapping.findForward(pageOf);
	}

	/**
	 * This method validates the formbean.
	 * @param request : request
	 * @param form : form
	 * @return String : String
	 */
	private String validate(HttpServletRequest request, QuickEventsForm form)
	{
		final Validator validator = new Validator();
		ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
		String pageOf = Constants.SUCCESS;
		if (errors == null)
		{
			errors = new ActionErrors();
		}
		if (form.getCheckedButton().equals("1")
				&& !validator.isValidOption(form.getSpecimenLabel()))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("quickEvents.specimenLabel")));
			pageOf = Constants.FAILURE;
		}
		if (form.getCheckedButton().equals("2") && Validator.isEmpty(form.getBarCode()))
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("quickEvents.barcode")));
			pageOf = Constants.FAILURE;
		}
		// resolved bug#4121
		int errorCount = 0;
		for (int iCount = 0; iCount < Constants.EVENT_PARAMETERS.length; iCount++)
		{
			if (!Constants.EVENT_PARAMETERS[iCount].equalsIgnoreCase(form
					.getSpecimenEventParameter()))
			{
				errorCount = errorCount + 1;
			}
		}
		if (errorCount == Constants.EVENT_PARAMETERS.length)
		{
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item.required",
					ApplicationProperties.getValue("valid.quickEvents.eventparameters")));
			pageOf = Constants.FAILURE;
		}
		this.saveErrors(request, errors);
		return pageOf;
	}

	/**
	 *
	 * @param sourceObject : sourceObject
	 * @param value : value
	 * @param bizlogic : bizlogic
	 * @return String : String
	 * @throws Exception : Exception
	 */
	private String isExistingSpecimen(String sourceObject, String value, IBizLogic bizlogic)
			throws Exception
	{
		String returnValue = "0";

		final String sourceObjectName = Specimen.class.getName();
		final String[] selectColumnName = {Constants.SYSTEM_IDENTIFIER};
		final String[] whereColumnName = {sourceObject, Status.ACTIVITY_STATUS.toString()};
		//"storageContainer."+Constants.SYSTEM_IDENTIFIER
		final String[] whereColumnCondition = {"=", "!="};
		final Object[] whereColumnValue = {value, Status.ACTIVITY_STATUS_DISABLED.toString()};
		final String joinCondition = Constants.AND_JOIN_CONDITION;

		final List list = bizlogic.retrieve(sourceObjectName, selectColumnName, whereColumnName,
				whereColumnCondition, whereColumnValue, joinCondition);

		Logger.out.debug("MD 04-July-06 : - ><><>< " + sourceObject + " : " + value);
		String specimenID = "0";
		if (!list.isEmpty())
		{
			final Object obj = list.get(0);
			Logger.out.debug("04-July-06 :- " + obj.getClass().getName());
			final Long specimen = (Long) obj;
			specimenID = specimen.toString();

			returnValue = specimenID;
		}
		else
		{
			returnValue = "0";
		}
		Logger.out.debug("MD 04-July-06 : - ><><>< " + sourceObject + " : " + value
				+ " Found SpecimenID:  " + returnValue);

		return returnValue;
	}

}
