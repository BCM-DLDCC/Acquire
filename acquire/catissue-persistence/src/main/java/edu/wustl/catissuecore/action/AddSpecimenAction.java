/**
 * <p>Title: CreateSpecimenAction Class>
 * <p>Description:	CreateSpecimenAction initializes the fields in the Create Specimen page.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Aniruddha Phadnis
 * @version 1.00
 */

package edu.wustl.catissuecore.action;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.CreateSpecimenForm;
import edu.wustl.catissuecore.domain.Specimen;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.bizlogic.IBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.common.util.global.Status;

/**
 * AddSpecimenAction gets the Specimen Id from Label or Barcode.
 * @author santosh_chandak
 */
public class AddSpecimenAction extends SecureAction
{

	/**
	 * Overrides the execute method of Action class.
	 * @param mapping : mapping
	 * @param form : form
	 * @param request : request
	 * @param response : response
	 * @throws IOException : IOException
	 * @throws ServletException : ServletException
	 * @throws BizLogicException : BizLogicException
	 * @return ActionForward : ActionForward
	 */
	@Override
	public ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException, BizLogicException
	{

		final CreateSpecimenForm createForm = (CreateSpecimenForm) form;
		final String pageOf = request.getParameter(Constants.PAGE_OF);

		final String sourceObjectName = Specimen.class.getName();
		//String[] selectColumnName = {Constants.SYSTEM_IDENTIFIER};

		final String[] whereColumnName = new String[1];
		final Object[] whereColumnValue = new Object[1];

		// checks whether label or barcode is selected
		if (createForm.getRadioButton().equals("1"))
		{
			whereColumnName[0] = Constants.SYSTEM_LABEL;
			whereColumnValue[0] = createForm.getParentSpecimenLabel().trim();
		}
		else
		{
			whereColumnName[0] = Constants.SYSTEM_BARCODE;
			whereColumnValue[0] = createForm.getParentSpecimenBarcode().trim();
		}

		//String[] whereColumnCondition = {"="};

		final IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
		final IBizLogic bizLogic = factory.getBizLogic(Constants.DEFAULT_BIZ_LOGIC);
		final List list = bizLogic.retrieve(sourceObjectName, whereColumnName[0],
				whereColumnValue[0]);

		/**
		 *  If list is not empty, set the Parent
		 *  specimen Id and forward to success.
		 *  If list is null or empty, forward to failure.
		 */
		if (list != null && !list.isEmpty())
		{
			final Specimen objSpecimen = (Specimen) list.get(0);

			if (objSpecimen.getActivityStatus().equals(Status.ACTIVITY_STATUS_DISABLED.toString()))
			{
				/**
				* Name : Falguni Sachde
				* Reviewer Name : Sachin Lale
				* Bug ID: 4919
				* Description: Added new error message and check for pageOf flow,
				* 	if user clicks directly derived
				* 			   link and specimen status is disabled
				*/
				final ActionErrors errors = this.getActionErrors(request);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
						"error.parentobject.disabled", "Specimen"));
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.derived",
						"Derived Specimen"));
				this.saveErrors(request, errors);
				return mapping.findForward(Constants.FAILURE);
			}
			else if (objSpecimen.getActivityStatus().equals(
					Status.ACTIVITY_STATUS_CLOSED.toString()))
			{
				/**
				* Name : Falguni Sachde
				* Reviewer Name : Sachin Lale
				* Bug ID: 4919
				* Description: Added new error message and check for pageOf flow,
				*  if user clicks directly derived
				* 			   link and specimen status is disabled
				*/
				final ActionErrors errors = this.getActionErrors(request);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.parentobject.closed",
						"Specimen"));
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.derived",
						"Derived Specimen"));
				this.saveErrors(request, errors);
				return mapping.findForward(Constants.FAILURE);
			}

			final Long specimen = objSpecimen.getId();
			createForm.setParentSpecimenId("" + specimen.longValue());
			createForm.setReset(false); // Will not reset the parameters
			if (pageOf != null && pageOf.equals(Constants.PAGE_OF_CREATE_SPECIMEN_CP_QUERY))
			{
				return mapping.findForward(Constants.PAGE_OF_CREATE_SPECIMEN_CP_QUERY);
			}
			return mapping.findForward(Constants.SUCCESS);
		}
		else
		{
			return mapping.findForward(Constants.FAILURE);
		}
	}

	/**
	 * This method returns the ActionErrors object present in the request scope.
	 * If it is absent method creates & returns new ActionErrors object.
	 * @param request object of HttpServletRequest
	 * @return ActionErrors
	 */
	private ActionErrors getActionErrors(HttpServletRequest request)
	{
		ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);

		if (errors == null)
		{
			errors = new ActionErrors();
		}

		return errors;
	}

	/**
	 * @param form : form
	 * @return String : String
	 */
	/*
		protected String getObjectId(AbstractActionForm form)
		{
			final CreateSpecimenForm createSpecimenForm = (CreateSpecimenForm) form;
			SpecimenCollectionGroup specimenCollectionGroup = null;
			if (createSpecimenForm.getParentSpecimenId() != null
					&& createSpecimenForm.getParentSpecimenId() != "")
			{
				final Specimen specimen = AppUtility.getSpecimen(createSpecimenForm
						.getParentSpecimenId());
				specimenCollectionGroup = specimen.getSpecimenCollectionGroup();
				final CollectionProtocolRegistration cpr = specimenCollectionGroup
						.getCollectionProtocolRegistration();
				if (cpr != null)
				{
					final CollectionProtocol cp = cpr.getCollectionProtocol();
					return Constants.COLLECTION_PROTOCOL_CLASS_NAME + "_" + cp.getId();
				}
			}
			return null;

		}
	*/
}